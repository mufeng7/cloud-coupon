package com.sztu.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则类型，枚举类
 */
@Getter
@AllArgsConstructor
public enum RuleFlag {
    //单类别
    MANJIAN("满减券的计算规则"),
    ZHEKOU("折扣券的计算规则"),
    LIJIAN("立减券的计算规则"),

    //多类别优惠券定义
    MANJIAN_ZHEJOU("满减券 + 折扣券的计算规则");
    //TODO 更多组合

    private String description;
}
