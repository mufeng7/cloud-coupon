package com.sztu.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.executor.ExecuteManager;
import com.sztu.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结算服务controller
 */
@Slf4j
@RestController
public class SettlementController {

    @Autowired
    public ExecuteManager executeManager;

    /**
     *http://localhost:9000/sztu/coupon-settlement/settlement/compute
     * @param settlement
     * @return
     * @throws CouponException
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement) throws CouponException{
        log.info("settlement: {}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }

    @GetMapping("/hello")
    public String hello(){
        log.debug("get success");
        return "seccuee";
    }

}
