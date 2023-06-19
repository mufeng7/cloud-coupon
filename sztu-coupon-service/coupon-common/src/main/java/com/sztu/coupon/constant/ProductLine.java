package com.sztu.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum ProductLine {

    TIANMAO("天猫",1),
    TAOBAO("淘宝",2);

    //产品线描述（分类）
    private String description;

    //产品线分类编码
    private Integer code;

    public static ProductLine of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(code + " not exist"));
    }
}
