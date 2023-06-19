package com.sztu.coupon.controller;

import com.sztu.coupon.entity.Coupon;
import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.service.IUserService;
import com.sztu.coupon.vo.AcquireTemplateRequest;
import com.sztu.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/findCouponByStatus")
    List<Coupon> findCouponByStatus(@RequestParam("userId") Long userId,@RequestParam("status") Integer status) throws CouponException {
        return userService.findCouponByStatus(userId,status);
    }

    @GetMapping("/findAvailableTemplate")
    List<CouponTemplateSDK> findAvailableTemplate(@RequestParam("userId") Long userId) throws CouponException{
        return userService.findAvailableTemplate(userId);
    }

    @PostMapping("/acquireTemplate")
    Coupon acquireTemplate(@RequestBody AcquireTemplateRequest request) throws CouponException{
        return userService.acquireTemplate(request);
    }

}
