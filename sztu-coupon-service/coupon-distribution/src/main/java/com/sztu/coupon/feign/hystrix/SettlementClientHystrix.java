package com.sztu.coupon.feign.hystrix;

import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.feign.SettlementClient;
import com.sztu.coupon.vo.CommonResponse;
import com.sztu.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结算服务熔断策略实现
 */
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlementInfo) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error");
        settlementInfo.setEmploy(false);
        settlementInfo.setCost(-1.0);
        return new CommonResponse<>(-1,"[eureka-client-coupon-settlement] request error",settlementInfo);
    }
}
