package com.sztu.coupon;

import com.alibaba.fastjson.JSON;
import com.sztu.coupon.constant.CouponCategory;
import com.sztu.coupon.constant.DistributeTarget;
import com.sztu.coupon.constant.PeriodType;
import com.sztu.coupon.constant.ProductLine;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.service.IBuildTemplateService;
import com.sztu.coupon.service.ITemplateBaseService;
import com.sztu.coupon.vo.CouponTemplateSDK;
import com.sztu.coupon.vo.TemplateRequest;
import com.sztu.coupon.vo.TemplateRule;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestConntion {

    @Autowired
    private IBuildTemplateService buildTemplateService;

    @Autowired
    private ITemplateBaseService baseService;



    @Test
    public void test01() throws Exception {

        System.out.println(JSON.toJSONString(buildTemplateService.buildTemplate(fakeTemplateRequest())));
        Thread.sleep(5000);
    }

    @Test
    public void contextLoad(){

    }

    private TemplateRequest fakeTemplateRequest(){
        TemplateRequest request = new TemplateRequest();
        request.setName("优惠券模板-" + new Date().getTime());
        request.setLogo("http://www.imooc.com");
        request.setDesc("一张优惠券");
        request.setCategory(CouponCategory.MANJIAN.getCode());
        request.setProductLine(ProductLine.TAOBAO.getCode());
        request.setCount(10000);
        request.setUserId(10000L);
        request.setTarget(DistributeTarget.SINGLE.getCode());
        TemplateRule rule = new TemplateRule();
        rule.setExpiration(new TemplateRule.Expiration(    //60天后过期
                PeriodType.SHIFT.getCode(), 1, DateUtils.addDays(new Date(),60).getTime()
        ));
        rule.setDiscount(new TemplateRule.Discount(5,1));
        rule.setLimitation(1);
        rule.setUsage(new TemplateRule.Usage("广东省","茂名市", JSON.toJSONString(Arrays.asList("文娱","生鲜"))));
        rule.setWeight(JSON.toJSONString(Collections.EMPTY_LIST));
        request.setRule(rule);
        return request;
    }


    @Test
    public void test03() throws CouponException {
        System.out.println("------------buildTemplateInfo----------------------");
        //System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(12)));
        //System.out.println(JSON.toJSONString(baseService.buildTemplateInfo(13)));
        System.out.println("------------------------------------------------------");
        System.out.println("---------------findAllUsableTemplate-------------------");
        //System.out.println(baseService.findAllUsableTemplate());
        System.out.println("------------------------------------------");
        System.out.println("----------------------------------");
        System.out.println(baseService.findIds2TemplateSDK(Arrays.asList(12,13)));
        System.out.println("----------------------------------");
    }
}
