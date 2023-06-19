package com.sztu.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 结算信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettlementInfo {

    private Long userId;

    private List<GoodsInfo> goodsInfos;

    private List<CouponAndTemplateInfo> couponAndTemplateInfos;

    //是否结算生效，即核销
    private Boolean employ;

    //结算金额
    private Double cost;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CouponAndTemplateInfo{
        //优惠券id
        private Integer id;

        private CouponTemplateSDK templateSDK;
    }

}
