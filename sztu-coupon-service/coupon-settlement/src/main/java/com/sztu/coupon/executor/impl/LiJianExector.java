package com.sztu.coupon.executor.impl;

import com.sztu.coupon.constant.RuleFlag;
import com.sztu.coupon.executor.AbstractExector;
import com.sztu.coupon.executor.RuleExecutor;
import com.sztu.coupon.vo.CouponTemplateSDK;
import com.sztu.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 立减优惠券结算规则执行器
 */
@Slf4j
@Component
public class LiJianExector extends AbstractExector implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.LIJIAN;
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {
        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo,goodsSum);
        if (null != probability){
            log.debug("LiJian Coupon Template Is not Math");
            return probability;
        }
        CouponTemplateSDK templateSDK = settlementInfo.getCouponAndTemplateInfos().get(0).getTemplateSDK();
        double quota = (double) templateSDK.getRule().getDiscount().getQuota();
        settlementInfo.setCost(
                retain2Decimals(goodsSum - quota) > minCost()
                ? retain2Decimals(goodsSum - quota) : minCost()
        );
        log.debug("User LiJian Coupon Match Goods Cost From {}, To {}",goodsSum,settlementInfo.getCost());
        return settlementInfo;
    }
}
