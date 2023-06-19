package com.sztu.coupon.service;

import com.sztu.coupon.entity.CouponTemplate;

/**
 * 异步服务接口定义
 */
public interface IAsyncService {
    /**
     * 根据优惠券模板异步创建优惠券
     * @param template
     */
    void asyncConstructCouponByATemplate(CouponTemplate template);
}
