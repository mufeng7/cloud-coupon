package com.sztu.coupon.executor;

import com.sztu.coupon.constant.CouponCategory;
import com.sztu.coupon.constant.RuleFlag;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券结算规则执行管理器
 * 即根据用户的请求找到对应的Executor
 * BeanPostProcessor:所有bean创建完后，再创建
 */
@Slf4j
@Component
public class ExecuteManager implements BeanPostProcessor {

    /**
     * 规则执行器映射
     */
    private static Map<RuleFlag,RuleExecutor> executorIndex = new HashMap<>(RuleFlag.values().length);

    public SettlementInfo computeRule(SettlementInfo settlement) throws CouponException {
        SettlementInfo result = null;
        if (settlement.getCouponAndTemplateInfos().size() == 1){
            CouponCategory category = CouponCategory.of(settlement.getCouponAndTemplateInfos().get(0).getTemplateSDK().getCategory());
            switch (category){
                case MANJIAN:
                    result = executorIndex.get(RuleFlag.MANJIAN).computeRule(settlement);
                    break;
                case ZHEKOU:
                    result = executorIndex.get(RuleFlag.ZHEKOU).computeRule(settlement);
                    break;
                case LIJIAN:
                    result = executorIndex.get(RuleFlag.LIJIAN).computeRule(settlement);
                    break;
            }
        }else {
            List<CouponCategory> categories = new ArrayList<>(settlement.getCouponAndTemplateInfos().size());
            settlement.getCouponAndTemplateInfos().forEach(
                    ct ->{
                        categories.add(CouponCategory.of(ct.getTemplateSDK().getCategory()));
                    }
            );
            if (categories.size() != 2){
                throw new CouponException("Not Support For More " + "Template Category");
            }else {
                if (categories.contains(CouponCategory.MANJIAN) && categories.contains(CouponCategory.ZHEKOU)){
                    result = executorIndex.get(RuleFlag.MANJIAN_ZHEJOU).computeRule(settlement);
                }else {
                    throw new CouponException("Not Support For Other Template Category");
                }
            }
        }
        return result;
    }


    /**
     * 再bean初始化之前执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof  RuleExecutor)){
            return bean;
        }
        RuleExecutor executor = (RuleExecutor) bean;
        RuleFlag ruleFlag = executor.ruleConfig();
        if (executorIndex.containsKey(ruleFlag)){
            throw new IllegalStateException("There is already on executor" + "for rule flag" + ruleFlag);
        }
        log.info("Load exector {} for rule {}.",executor.getClass(),ruleFlag);
        executorIndex.put(ruleFlag,executor);
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    /**
     * 再bean初始化之后执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
