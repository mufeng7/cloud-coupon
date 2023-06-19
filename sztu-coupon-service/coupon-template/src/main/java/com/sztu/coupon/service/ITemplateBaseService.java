package com.sztu.coupon.service;

import com.sztu.coupon.entity.CouponTemplate;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ITemplateBaseService {

    /**
     * 根据优惠券模板id，获取优惠券模板信息
     * @param id
     * @return
     * @throws CouponException
     */
    CouponTemplate buildTemplateInfo(Integer id) throws CouponException;

    /**
     * 查找所有可以的优惠券模板
     * @return
     */
    List<CouponTemplateSDK> findAllUsableTemplate();

    /**
     * 获取模板ids到CouponTemplateSDK的映射
     * @param ids
     * @return
     */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
