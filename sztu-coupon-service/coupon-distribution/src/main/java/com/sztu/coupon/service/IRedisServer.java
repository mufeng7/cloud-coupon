package com.sztu.coupon.service;


import com.sztu.coupon.entity.Coupon;
import com.sztu.coupon.exception.CouponException;

import java.util.List;

public interface IRedisServer {

    /**
     * 查找优惠券换成的优惠券列表数据
     * @param userId
     * @param status
     * @return
     */
    List<Coupon> getCachedCoupons(Long userId,Integer status);

    /**
     * 避免缓存穿透
     * @param userId
     * @param status
     */
    void setEmptyCouponListToCache(Long userId,List<Integer> status);

    /**
     * 尝试从cache中获取一个优惠券码
     * @param templateId
     * @return
     */
    String tryToAcquireCouponFromCache(Integer templateId);

    /**
     * 把优惠券放进缓存中
     * @param userId
     * @param coupons
     * @param status
     * @return 成功的个数
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId,List<Coupon> coupons,Integer status) throws CouponException;

}
