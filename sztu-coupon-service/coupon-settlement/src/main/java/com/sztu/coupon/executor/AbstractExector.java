package com.sztu.coupon.executor;

import com.alibaba.fastjson.JSON;
import com.sztu.coupon.vo.GoodsInfo;
import com.sztu.coupon.vo.SettlementInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class AbstractExector {

    /**
     * 校验商品类型与优惠券是否匹配
     * 1、实现单品类的校验，多品类的定义重装这个方法
     * 2.商品只需要一个优惠券要求的商品类型去匹配就可以
     * @param settlement
     * @return
     */
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement){
        List<Integer> goodsType = settlement.getGoodsInfos()
                .stream()
                .map(GoodsInfo::getType)
                .collect(Collectors.toList());

        List<Integer> templateGoodsType = JSON.parseObject(
                settlement.getCouponAndTemplateInfos().get(0).getTemplateSDK().getRule().getUsage().getGoodsType(),
                List.class
        );
        return CollectionUtils.isNotEmpty(CollectionUtils.intersection(goodsType,templateGoodsType));
        //List<Integer> templateGoodsType
    }


    /**
     * 处理商品类型与优惠券不匹配的情况
     * @param settlement
     * @param goodsSum
     * @return
     */
    protected SettlementInfo processGoodsTypeNotSatisfy(SettlementInfo settlement, double goodsSum){
        boolean isGoodsType = isGoodsTypeSatisfy(settlement);

        //当商品类型不满足时，直接返回总价
        if (!isGoodsType){
            settlement.setCost(goodsSum);
            settlement.setCouponAndTemplateInfos(Collections.emptyList());
            return settlement;
        }
        return null;
    }

    /**
     * 计算总价
     */
    public double goodsCostSum(List<GoodsInfo> goodsInfos){
        return goodsInfos.stream()
                .mapToDouble(g -> g.getPrice() * g.getCount())
                .sum();
    }


    /**
     * 保留两位小数
     * @param value
     * @return
     */
    protected double retain2Decimals(double value){
        return new BigDecimal(value).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    protected double minCost(){
        return 0.1;
    }

}
