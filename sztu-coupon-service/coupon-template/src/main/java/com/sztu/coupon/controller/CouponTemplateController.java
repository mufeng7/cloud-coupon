package com.sztu.coupon.controller;

import com.sztu.coupon.entity.CouponTemplate;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.service.IBuildTemplateService;
import com.sztu.coupon.service.ITemplateBaseService;
import com.sztu.coupon.vo.CouponTemplateSDK;
import com.sztu.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CouponTemplateController {

    private final IBuildTemplateService buildTemplateService;

    private final ITemplateBaseService templateBaseService;

    public CouponTemplateController(IBuildTemplateService buildTemplateService, ITemplateBaseService templateBaseService) {
        this.buildTemplateService = buildTemplateService;
        this.templateBaseService = templateBaseService;
    }

    /**
     * 127.0.0.1:7001/coupon-template/template/build
     * 127.0.0.1:9000/sztu/coupon-template/template/build      网关访问
     * @param request
     * @return
     * @throws CouponException
     */
    @PostMapping("/template/build")
    public CouponTemplate buildTemplate(@RequestBody TemplateRequest request) throws CouponException{
        return buildTemplateService.buildTemplate(request);
    }

    /**
     * 构造优惠券模板详情
     * @param id
     * @return
     * @throws CouponException
     */
    @GetMapping("/template/info")
    public CouponTemplate buildTemplateInfo(@RequestParam("id") Integer id) throws CouponException{
        return templateBaseService.buildTemplateInfo(id);
    }

    /**
     * 提供给第三方使用
     * 查找所有可用的优惠券模板
     * @return
     */
    @GetMapping("/template/sdk/all")
    public List<CouponTemplateSDK> findAllUsableTemplate(){
        return templateBaseService.findAllUsableTemplate();
    }

    @GetMapping("/template/sdk/infos")
    public Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(@RequestParam("ids") Collection<Integer> ids){
        return templateBaseService.findIds2TemplateSDK(ids);
    }
}
