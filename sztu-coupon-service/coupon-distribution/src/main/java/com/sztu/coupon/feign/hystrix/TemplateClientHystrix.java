package com.sztu.coupon.feign.hystrix;

import com.sztu.coupon.feign.TemplateClient;
import com.sztu.coupon.vo.CommonResponse;
import com.sztu.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 优惠券模板Feign接口定义
 */
@Slf4j
@Component
public class TemplateClientHystrix implements TemplateClient {

    @Override
    public CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate() {
        log.error("[eureka-client-coupon-template] findAllUsableTemplate() request error" );
        return new CommonResponse<>(-1,"[eureka-client-coupon-template] request error", Collections.emptyList());
    }

    @Override
    public CommonResponse<Map<Integer, CouponTemplateSDK>> findIds2TemplateSDK(Collection<Integer> ids) {
        log.error("[eureka-client-coupon-template] findIds2TemplateSDK request error" );
        return new CommonResponse<>(-1,"[eureka-client-coupon-template] request error", new HashMap<>());
    }
}
