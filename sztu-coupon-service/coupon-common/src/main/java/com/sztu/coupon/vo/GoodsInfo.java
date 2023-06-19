package com.sztu.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfo {

    private Integer type;

    private Double price;

    private Integer count;

    //TODO  名称，使用信息

}
