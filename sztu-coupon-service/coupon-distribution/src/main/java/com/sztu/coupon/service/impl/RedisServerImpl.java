package com.sztu.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.sztu.coupon.constant.Constant;
import com.sztu.coupon.constant.CouponStatus;
import com.sztu.coupon.entity.Coupon;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.service.IRedisServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisServerImpl implements IRedisServer {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<Coupon> getCachedCoupons(Long userId, Integer status) {
        log.info("Get Coupons From Cache: {}, {}",userId,status);
        String redisKey = status2RedisKey(status,userId);
        //TODO 为什么要变成string、
        //这里有问题
        List<Object> values = redisTemplate.opsForHash().values(redisKey);
        if (CollectionUtils.isEmpty(values)){
            setEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        List<String> coupons = values
                .stream()
                .map(o -> Objects.toString(o,null))
                .collect(Collectors.toList());
//        if (CollectionUtils.isEmpty(coupons)){
//            setEmptyCouponListToCache(userId, Collections.singletonList(status));
//            return Collections.emptyList();
//        }

        return coupons.stream()
                .map(cs -> JSON.parseObject(cs,Coupon.class))
                .collect(Collectors.toList());
    }


    /**
     * 避免缓存穿透
     *优惠券结构：K：status对应string + userId
     *          V：（K：couponID，V：序列化的Coupon）
     * @param userId
     * @param status
     */
    @Override
    @SuppressWarnings("all")
    public void setEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save EMpty List To Cache For User: {}, Status: {}",userId, JSON.toJSON(status));
        Map<String,String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1",JSON.toJSONString(Coupon.invalidCoupon()));
        //使用SessionCallBack把数据命令放入到Redis的pipeline中
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(
                        s ->{
                            String redisKey = status2RedisKey(s,userId);
                            redisOperations.opsForHash().putAll( redisKey,invalidCouponMap);
                        }
                );
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    @Override
    public String tryToAcquireCouponFromCache(Integer templateId) {
        String redisKey = String.format("%s%s",Constant.RedisPrefix.COUPON_TEMPLATE,templateId);
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code: {}, {}, {}",templateId,redisKey,couponCode);
        return couponCode;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache: {}, {}, {}",userId,JSON.toJSONString(coupons),status);
        Integer result = -1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                result = addCouponToCacheForUsable(userId,coupons);
                break;
            case USERD:
                result = addCouponToCacheForUsed(userId,coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId,coupons);
                break;
        }
        return result;
    }


    /**
     * 根据status获取对应的redis key
     * @param status
     * @param userId
     * @return
     */
    private String status2RedisKey(Integer status, Long userId){
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPrefix.USER_COUPON_USABLE,userId);
                break;
            case USERD:
                redisKey = String.format("%s%s",Constant.RedisPrefix.USER_COUPON_USED,userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",Constant.RedisPrefix.USER_COUPON_EXPIRED,userId);
                break;
        }
        return redisKey;
    }

    private Long getRandomExpirationTime(Integer min,Integer max){
        return RandomUtils.nextLong(min * 60 * 60,max *60 *60);
    }

    private Integer addCouponToCacheForUsable(Long userId,List<Coupon> coupons){
        log.info("Add Coupon To Cache For Usable: {}, {}",userId,JSON.toJSONString(coupons));
        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(),userId);
        Map<String,String> needCacheObject = new HashMap<>();
        coupons.forEach(
                c -> needCacheObject.put(c.getId().toString(),JSON.toJSONString(c))
        );
        redisTemplate.opsForHash().putAll(redisKey,needCacheObject);
        redisTemplate.expire(redisKey,getRandomExpirationTime(1,2), TimeUnit.SECONDS);
        return needCacheObject.size();
    }

    private Integer addCouponToCacheForUsed(Long userId,List<Coupon> coupons) throws CouponException{
        log.info("Add Coupon To Cache For Used: {}, {}",userId,JSON.toJSONString(coupons));
        Map<String,String> needCacheForUsed = new HashMap<>(coupons.size());
        String redisKeyForUsed = status2RedisKey(CouponStatus.USERD.getCode(),userId);
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        List<Coupon> usableCaches = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        assert usableCaches.size() > coupons.size();
        List<Integer> usableIds = usableCaches.stream()
                .map(o -> o.getId())
                .collect(Collectors.toList());
        List<Integer> usedIds = coupons.stream()
                .map(o -> o.getId())
                .collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(usableIds,usedIds)){
            log.error("CurCoupon is not Equal ToCache: {},{},{}",userId,JSON.toJSONString(usableIds),JSON.toJSONString(usedIds));
            throw new CouponException("CurCoupon is not Equal ToCache");
        }

        coupons.forEach(
                t -> needCacheForUsed.put(t.getId().toString(),JSON.toJSONString(t))
        );

        List<String> needCleanKeys = usedIds.stream()
                .map(o -> o.toString())
                .collect(Collectors.toList());
        // TODO  真的可以确保一致性吗，是否需要lua脚本
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {
                operations.opsForHash().putAll(redisKeyForUsed,needCacheForUsed);
                operations.opsForHash().delete(redisKeyForUsable,needCleanKeys);
                //重置过期时间
                operations.expire(redisKeyForUsed,getRandomExpirationTime(1,2),TimeUnit.SECONDS);
                operations.expire(redisKeyForUsable,getRandomExpirationTime(1,2),TimeUnit.SECONDS);

                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }


    /**
     * 将过期优惠券，加入cache中
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    private Integer addCouponToCacheForExpired(Long userId,List<Coupon> coupons) throws CouponException{
        log.info("Add Coupon To Cache For Expired: {}, {}",userId,JSON.toJSONString(coupons));
        Map<String,String> needCacheForExpired = new HashMap<>(coupons.size());
        String redisKeyForExpired = status2RedisKey(CouponStatus.EXPIRED.getCode(), userId);
        String redisKeyForUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());

        assert curUsableCoupons.size() > coupons.size();
        List<Integer> usableIds = curUsableCoupons.stream()
                .map(Coupon::getId)
                .collect(Collectors.toList());
        List<Integer> expiredIds = coupons.stream()
                .map(o -> o.getId())
                .collect(Collectors.toList());
        if (!CollectionUtils.isSubCollection(usableIds,expiredIds)){
            log.error("CurExpiredCoupon is not Equal ToCache: {},{},{}",userId,JSON.toJSONString(usableIds),JSON.toJSONString(expiredIds));
            throw new CouponException("CurExpiredCoupon is not Equal ToCache");
        }
        coupons.forEach(
                t -> needCacheForExpired.put(t.getId().toString(),JSON.toJSONString(t))
        );
        List<String> needCleanKeys = expiredIds.stream()
                .map(o -> o.toString())
                .collect(Collectors.toList());
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.opsForHash().putAll(redisKeyForExpired,needCacheForExpired);
                operations.opsForHash().delete(redisKeyForUsable,needCleanKeys);        //是否需要toArray
                operations.expire(redisKeyForExpired,getRandomExpirationTime(1,2),TimeUnit.SECONDS);
                operations.expire(redisKeyForUsable,getRandomExpirationTime(1,2),TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Exe Result: {}",JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }


}
