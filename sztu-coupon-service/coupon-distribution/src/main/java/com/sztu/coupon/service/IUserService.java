package com.sztu.coupon.service;

import com.sztu.coupon.entity.Coupon;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.vo.AcquireTemplateRequest;
import com.sztu.coupon.vo.CouponTemplateSDK;
import com.sztu.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * 1.用户三类状态优惠券信息服务展示
 * 2.查看用户当前可以领取的优惠券模板     与coupon-template 微服务配合实现
 * 3.用户领取优惠券服务
 * 4.用户消费优惠券服务                 与coupon-settlement微服务实现
 */
public interface IUserService {

    List<Coupon> findCouponByStatus(Long userId,Integer status) throws CouponException;

    /**
     *查看当前可以领取的模板
     * @param userId
     * @return
     * @throws CouponException
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException;

    /**
     * 用户领取优惠券
     * @param request
     * @return
     * @throws CouponException
     */
    Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException;

    /**
     * 结算优惠券
     * @param info
     * @return
     * @throws CouponException
     */
    SettlementInfo settlement(SettlementInfo info) throws CouponException;

}
