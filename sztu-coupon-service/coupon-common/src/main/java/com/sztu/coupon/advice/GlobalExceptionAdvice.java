package com.sztu.coupon.advice;

import com.sztu.coupon.exception.CouponException;
import com.sztu.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局处理异常
 */

@RestControllerAdvice
public class GlobalExceptionAdvice {


    /**
     * 对CouponException进行统一处理
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest request, CouponException exception){
        CommonResponse<String> response = new CommonResponse<>(-1,"business error");
        response.setData(exception.getMessage());
        return response;
    }
}
