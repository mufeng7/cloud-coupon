package com.sztu.coupon.feign;

import com.sztu.coupon.feign.hystrix.TemplateClientHystrix;
import com.sztu.coupon.vo.CommonResponse;
import com.sztu.coupon.vo.CouponTemplateSDK;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板微服务Feign接口定义
 */
@FeignClient(value = "eureka-client-coupon-template",fallback = TemplateClientHystrix.class)
public interface TemplateClient {
    /**
     * 查找所有可以的优惠券模板
     * @return
     */
    @RequestMapping(value = "/coupon-template/template/sdk/all",method = RequestMethod.GET)
    CommonResponse<List<CouponTemplateSDK>> findAllUsableTemplate();

    /**
     * 获取模板ids到SDK
     * @return
     */
    @RequestMapping(value = "/coupon-template/template/sdk/infos",method = RequestMethod.GET)
    CommonResponse<Map<Integer,CouponTemplateSDK>> findIds2TemplateSDK(@RequestParam("ids")Collection<Integer> ids);
}
