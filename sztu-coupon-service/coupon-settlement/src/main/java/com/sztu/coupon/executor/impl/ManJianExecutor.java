package com.sztu.coupon.executor.impl;

import com.sztu.coupon.constant.RuleFlag;
import com.sztu.coupon.executor.AbstractExector;
import com.sztu.coupon.executor.RuleExecutor;
import com.sztu.coupon.vo.CouponTemplateSDK;
import com.sztu.coupon.vo.SettlementInfo;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;


/**
 * 满减优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManJianExecutor extends AbstractExector implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {

        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo,goodsSum);
        if (null != probability){
            log.debug("MianJian Template Is not Match To GoodsType");
            return probability;
        }
        //判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplateSDK();
        double base = (double)templateSDK.getRule().getDiscount().getBase();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();
        if (goodsSum < base){
            log.debug("Current Cost Sum < ManJian Coupon Base");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }
        settlementInfo.setCost(retain2Decimals(
                (goodsSum - quota) > minCost() ? (goodsSum - quota) : minCost()
        ));
        log.debug("User Mianjian Coupon Make Goods Cost From {} To {}",goodsSum,settlementInfo.getCost());

        return settlementInfo;
    }
}
