package com.sztu.coupon.service;

import com.sztu.coupon.entity.CouponTemplate;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.vo.TemplateRequest;

public interface IBuildTemplateService {

    CouponTemplate buildTemplate(TemplateRequest request) throws CouponException;
}
