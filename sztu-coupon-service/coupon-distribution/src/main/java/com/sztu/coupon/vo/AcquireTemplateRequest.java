package com.sztu.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcquireTemplateRequest {

    private Long userId;

    //优惠券模板信息
    private CouponTemplateSDK templateSDK;

}
