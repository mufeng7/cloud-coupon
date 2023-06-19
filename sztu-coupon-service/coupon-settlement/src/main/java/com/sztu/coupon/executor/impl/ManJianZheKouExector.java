package com.sztu.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.sztu.coupon.constant.CouponCategory;
import com.sztu.coupon.constant.RuleFlag;
import com.sztu.coupon.executor.AbstractExector;
import com.sztu.coupon.executor.RuleExecutor;
import com.sztu.coupon.vo.GoodsInfo;
import com.sztu.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 满减 + 折扣优惠券结算规则执行器
 */
@Slf4j
@Component
public class ManJianZheKouExector extends AbstractExector implements RuleExecutor {
    @Override
    public RuleFlag ruleConfig() {
        return RuleFlag.MANJIAN_ZHEJOU;
    }

    /**
     * 用户传递的结算信息，与优惠券是否匹配
     * @param settlement
     * @return
     */
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        log.debug("Check ManJIan And ZheKou Is Match Or Not");
        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();
        settlement.getCouponAndTemplateInfos().forEach(
                ct ->{
                    templateGoodsType.addAll(JSON.parseObject(ct.getTemplateSDK().getRule().getUsage().getGoodsType(),List.class));
                }
        );
        return CollectionUtils.isEmpty(CollectionUtils.subtract(goodsType,templateGoodsType));
    }

    @Override
    public SettlementInfo computeRule(SettlementInfo settlementInfo) {

        double goodsSum = retain2Decimals(goodsCostSum(settlementInfo.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(settlementInfo,goodsSum);
        if (null != probability){
            log.debug("MianJian And ZheKou Template Is not Match TO GoodsType");
            return probability;
        }
        SettlementInfo.CouponAndTemplateInfo manjian = null;
        SettlementInfo.CouponAndTemplateInfo zhekou = null;
        for (SettlementInfo.CouponAndTemplateInfo ct : settlementInfo.getCouponAndTemplateInfos()){
            if (CouponCategory.of(ct.getTemplateSDK().getCategory()) == CouponCategory.MANJIAN){
                manjian = ct;
            }else {
                zhekou = ct;
            }
        }
        assert null != manjian;
        assert null != zhekou;

        if (!isTemplateCanShared(manjian,zhekou)){
            log.debug("Current ManJian And Zhejou Can not Shared");
            settlementInfo.setCost(goodsSum);
            settlementInfo.setCouponAndTemplateInfos(Collections.emptyList());
            return settlementInfo;
        }
        List<SettlementInfo.CouponAndTemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double) manjian.getTemplateSDK().getRule().getDiscount().getBase();
        double manJianQuota = (double) manjian.getTemplateSDK().getRule().getDiscount().getQuota();
        //最终价格
        //满减
        double targetSum = goodsSum;
        if (targetSum >= manJianBase){
            targetSum -= manJianQuota;
            ctInfos.add(manjian);
        }
        //计算折扣
        double zheKouQuota = (double) zhekou.getTemplateSDK().getRule().getDiscount().getQuota();
        targetSum *= zheKouQuota * 1.0 / 100;
        ctInfos.add(zhekou);
        settlementInfo.setCost(retain2Decimals(targetSum > minCost() ? targetSum : minCost()));
        settlementInfo.setCouponAndTemplateInfos(ctInfos);
        log.debug("Use Both Coupon");
        return settlementInfo;
    }


    /**
     * 去校验当前两种优惠券是否可以公用
     * @param manjian
     * @param zhekou
     * @return
     */
    private boolean isTemplateCanShared(SettlementInfo.CouponAndTemplateInfo manjian,
                                       SettlementInfo.CouponAndTemplateInfo zhekou){
        String manjianKey = manjian.getTemplateSDK().getKey() + String.format("%04d",manjian.getTemplateSDK().getId());
        String zhekouKey = zhekou.getTemplateSDK().getKey() + String.format("%04d",zhekou.getTemplateSDK().getId());
        List<String> allSharedKeyForManjian = new ArrayList<>();
        allSharedKeyForManjian.add(manjianKey);
        allSharedKeyForManjian.addAll(JSON.parseObject(manjian.getTemplateSDK().getRule().getWeight(),List.class));

        List<String> allSharedKeyForZhekou = new ArrayList<>();
        allSharedKeyForZhekou.add(zhekouKey);
        allSharedKeyForZhekou.addAll(JSON.parseObject(zhekou.getTemplateSDK().getRule().getWeight(),List.class));

        return CollectionUtils.isSubCollection(Arrays.asList(manjianKey,zhekouKey),allSharedKeyForManjian)
                || CollectionUtils.isSubCollection(Arrays.asList(manjian,zhekouKey), allSharedKeyForZhekou);
    }
}
