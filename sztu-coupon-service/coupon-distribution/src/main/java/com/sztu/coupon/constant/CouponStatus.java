package com.sztu.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum CouponStatus {

    USABLE("可用的",1),
    USERD("已使用的",2),
    EXPIRED("已过期的(未被使用的)",3);

    private String description;

    private Integer code;

    /**
     * 根据code，获取枚举类
     */
    public static CouponStatus of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exists"));
    }

}
