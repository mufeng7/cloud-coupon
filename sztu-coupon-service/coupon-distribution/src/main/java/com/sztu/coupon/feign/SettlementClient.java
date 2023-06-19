package com.sztu.coupon.feign;

import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.feign.hystrix.SettlementClientHystrix;
import com.sztu.coupon.vo.CommonResponse;
import com.sztu.coupon.vo.SettlementInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "eureka-client-coupon-settlement",fallback = SettlementClientHystrix.class)
public interface SettlementClient {

    /**
     * 优惠券计算规则
     * @param settlementInfo
     * @return
     * @throws CouponException
     */
    @RequestMapping(value = "/coupon-settlement/settlement/compute",method = RequestMethod.POST)
    CommonResponse<SettlementInfo> computeRule(@RequestBody SettlementInfo settlementInfo) throws CouponException;
}
