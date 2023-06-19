package com.sztu.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微服务之间的优惠券模板学习定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponTemplateSDK {
    private Integer id;

    private String name;

    private String logo;

    private String desc;

    private String category;

    private Integer productLine;

    private String key;

    private Integer target;

    private TemplateRule rule;
}
