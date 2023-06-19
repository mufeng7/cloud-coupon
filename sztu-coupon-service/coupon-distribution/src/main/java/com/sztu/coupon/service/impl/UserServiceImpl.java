package com.sztu.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.sztu.coupon.constant.Constant;
import com.sztu.coupon.constant.CouponStatus;
import com.sztu.coupon.dao.CouponDao;
import com.sztu.coupon.entity.Coupon;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.feign.SettlementClient;
import com.sztu.coupon.feign.TemplateClient;
import com.sztu.coupon.service.IKafkaService;
import com.sztu.coupon.service.IRedisServer;
import com.sztu.coupon.service.IUserService;
import com.sztu.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户相关服务接口实现
 * 所有操作过程，状态都保存在redis中，并且通过Kafka吧消息对了传递到mysql中
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final CouponDao couponDao;

    private final IRedisServer redisServer;

    private final KafkaTemplate kafkaTemplate;

    private final TemplateClient templateClient;

    private final SettlementClient settlementClient;

    public UserServiceImpl(CouponDao couponDao, IRedisServer redisServer, KafkaTemplate kafkaTemplate,
                           TemplateClient templateClient, SettlementClient settlementClient) {
        this.couponDao = couponDao;
        this.redisServer = redisServer;
        this.kafkaTemplate = kafkaTemplate;
        this.templateClient = templateClient;
        this.settlementClient = settlementClient;
    }

    @Override
    public List<Coupon> findCouponByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> curCached = redisServer.getCachedCoupons(userId,status);
        List<Coupon> preTarget;
        if (CollectionUtils.isNotEmpty(curCached)){
            log.debug("coupon cache is not empty: {}, {}",userId,status);
            preTarget = curCached;
        }else {
            log.debug("coupon cache is empty, get coupon from db: {},{}",userId,status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            if (CollectionUtils.isEmpty(dbCoupons)){
                log.debug("current user do not have coupon: {},{}",userId,status);
                return dbCoupons;
            }
            //保存到缓存中，因为缓存中有templateSDK
            Map<Integer,CouponTemplateSDK> id2TemplateSDK = templateClient.findIds2TemplateSDK(
                    dbCoupons.stream()
                            .map(Coupon::getTemplateId)
                            .collect(Collectors.toList())
            ).getData();
            dbCoupons.forEach(
                    dc -> dc.setTemplateSDK(id2TemplateSDK.get(dc.getTemplateId()))
            );
            //数据库中存在记录，写回缓存
            //里面有张为-1的假优惠券
            preTarget = dbCoupons;
            redisServer.addCouponToCache(userId,preTarget,status);
        }
        //将无效优惠券剔除
        preTarget = preTarget.stream()
                .filter(c -> c.getId() != 1)
                .collect(Collectors.toList());
        //如果但钱获取的是可以优惠券，就需要对过期优惠券延迟处理
        if (CouponStatus.of(status) == CouponStatus.USABLE){
            CouponClassify classify = CouponClassify.classify(preTarget);
            if (CollectionUtils.isNotEmpty(classify.getExpired())){
                log.info("Add Expired Coupons To Cache From FindCouponsByStatus: {},{}",userId,status);
                redisServer.addCouponToCache(userId, classify.getExpired(),CouponStatus.EXPIRED.getCode());
                //发送到kafka中异步处理
                kafkaTemplate.send(Constant.TOPIC, JSON.toJSONString(new CouponKafkaMessage(
                        CouponStatus.EXPIRED.getCode(), classify.getExpired().stream().map(Coupon::getId).collect(Collectors.toList())
                )));
            }
        }
        return preTarget;
    }

    /**
     *查看当前可以领取的模板
     * @param userId
     * @return
     * @throws CouponException
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long curTime = new Date().getTime();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getData();
        log.debug("Find All Template From Template Client Count: {}",templateSDKS.size());
        //过滤过期优惠券模板
        templateSDKS = templateSDKS.stream()
                .filter(t -> t.getRule().getExpiration().getDeadline() > curTime)
                .collect(Collectors.toList());
        log.info("Find Usable Template Count: {}",templateSDKS.size());
        //把获取到的优惠券模板，转换成map
        //key是templateId value中left是template limitation(最多可以领取几张)，right是优惠券模板
        Map<Integer, Pair<Integer,CouponTemplateSDK>> limit2Template = new HashMap<>();
        templateSDKS.forEach(
                t -> limit2Template.put(t.getId(),Pair.of(t.getRule().getLimitation(),t))
        );
        //获取用户当前可以的优惠券模板信息
        List<Coupon> userUsableCoupons = findCouponByStatus(userId,CouponStatus.USABLE.getCode());
        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());

        Map<Integer,List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                        .collect(Collectors.groupingBy(Coupon::getTemplateId));

        limit2Template.forEach(
                (k,v) -> {
                    int limitation = v.getLeft();
                    CouponTemplateSDK templateSDK = v.getRight();
                    if (templateId2Coupons.containsKey(k) && templateId2Coupons.get(k).size() >= limitation){
                        return;
                    }
                    result.add(templateSDK);
                }
        );
        return result;
    }

    /**
     * 领取优惠券
     * 1.从TemplateClient拿到对应的优惠券，并检查是否过期
     * 2.根据limitation判断用户是否可以领取
     * 3.save to db
     * 4.填充CouponTemplateSDK
     * 5.save to cache
     * @param request
     * @return
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        Map<Integer,CouponTemplateSDK> id2Template = templateClient.findIds2TemplateSDK(Collections.singletonList(request.getTemplateSDK().getId())).getData();
        if (id2Template.size() <= 0){
            log.error("Can not Acquire Template From TemplateClient: {},{}",request.getTemplateSDK().getId());
            throw new CouponException("Can not Acquire Template From TemplateClient");
        }
        //用户是否可以领取这张优惠券
        List<Coupon> userUsableCoupons = findCouponByStatus(request.getUserId(),CouponStatus.USABLE.getCode());
        Map<Integer,List<Coupon>> templateId2Coupons = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        //如果领取过，并且领取的次数大于限制的次数就抛出异常
        if (templateId2Coupons.containsKey(request.getTemplateSDK().getId()) &&
        templateId2Coupons.get(request.getTemplateSDK().getId()).size() >= request.getTemplateSDK().getRule().getLimitation()){
            log.error("Exceed Template Assign Limiation: {}",request.getTemplateSDK().getId());
            throw new CouponException("Exceed Template Assign Limiation");
        }

        String couponCode =redisServer.tryToAcquireCouponFromCache(request.getTemplateSDK().getId());
        if (StringUtils.isEmpty(couponCode)){
            log.error("Can not Acquire Coupon Code: {}",request.getTemplateSDK().getId());
            throw new CouponException("Can not Acquire Coupon Code");
        }
        Coupon newCoupon = new Coupon(request.getTemplateSDK().getId(),request.getUserId(),couponCode,CouponStatus.USABLE);
        newCoupon = couponDao.save(newCoupon);
        newCoupon.setTemplateSDK(request.getTemplateSDK());
        redisServer.addCouponToCache(request.getUserId(),Collections.singletonList(newCoupon),CouponStatus.USABLE.getCode());
        return newCoupon;
    }

    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        return null;
    }
}
