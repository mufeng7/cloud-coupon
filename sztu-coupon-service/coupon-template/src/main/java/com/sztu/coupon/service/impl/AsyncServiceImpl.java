package com.sztu.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.sztu.coupon.constant.Constant;
import com.sztu.coupon.dao.CouponTemplateDao;
import com.sztu.coupon.entity.CouponTemplate;
import com.sztu.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    private final CouponTemplateDao templateDao;

    private final StringRedisTemplate redisTemplate;

    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 构建模板异步创建优惠券码
     * @param template
     */
    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByATemplate(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();

        Set<String> couponCodes = buildCouponCode(template);

        String redisKey = String.format("%s%s", Constant.RedisPrefix.COUPON_TEMPLATE,template.getId().toString());
        log.info("Push Coupon To Redis: {}",redisTemplate.opsForList().rightPushAll(redisKey,couponCodes));
        template.setAvailable(true);
        templateDao.save(template);
        watch.stop();
        log.info("Construct CouponCode By Template Cost: {}ms",watch.elapsed(TimeUnit.MILLISECONDS));
        //TODO  发送邮件模板已经可用
        log.info("CouponTemplate({}) Is Available",template.getId());
    }

    /**
     * 构建优惠券码
     * //优惠券唯一编码 = 4(产品线和类型) + 6(日期：221128) + 8位随机数  id(扩充为4位)
     * @param template
     * @return
     */
    @SuppressWarnings("all")
    private Set<String> buildCouponCode(CouponTemplate template){
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> result = new HashSet<>(template.getCount());
        //前四位
        String prefix4 = template.getProductLine().getCode().toString() + template.getCategory().getCode();
        String date = new SimpleDateFormat("yyMMdd").format(template.getCreateTime());
        for (int i = 0; i < template.getCount(); i++) {
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        //防止创建了重复的优惠券码，而导致数量不够
        while (result.size() < template.getCount()){
            result.add(prefix4 + buildCouponCodeSuffix14(date));
        }
        assert result.size() == template.getCount();
        log.info("Build Coupon Code  Cost:  {}ms",watch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }


    /**
     * 构造优惠券码后的14位
     * @param date
     * @return   14位优惠券码
     */
    private String buildCouponCodeSuffix14(String date){
        char[] bases = new char[]{'1','2','3','4','5','6','7','8','9'};
        //中间6位
        List<Character> chars = date.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        String mid6 = chars.stream().map(Objects::toString).collect(Collectors.joining());
        //后8位
        String suffix8 = RandomStringUtils.random(1,bases) + RandomStringUtils.randomNumeric(7);
        return mid6 + suffix8;
    }
}
