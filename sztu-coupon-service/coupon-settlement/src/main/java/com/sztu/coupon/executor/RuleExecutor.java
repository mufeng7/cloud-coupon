package com.sztu.coupon.executor;

import com.sztu.coupon.constant.RuleFlag;
import com.sztu.coupon.vo.SettlementInfo;

/**
 * 优惠券模板处理规则定义
 */
public interface RuleExecutor {

    /**
     * 对应到哪个规则
     * @return
     */
    RuleFlag ruleConfig();

    /**
     * 优惠券规则计算
     * @param settlementInfo，选择的优惠券
     * @return
     */
    SettlementInfo computeRule(SettlementInfo settlementInfo);


}
