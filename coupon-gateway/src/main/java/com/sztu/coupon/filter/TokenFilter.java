package com.sztu.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验请求中传递的token
 */
@Slf4j
//@Component
public class TokenFilter extends AbstractPreZuulFilter{
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        //log.info(String.format("%s request.getRequestURL to %s"),request.getMethod(),request.getRequestURL().toString());
        Object token = request.getParameter("token");
        if (null == token){
            log.error("error: token is empty");
            return fail(401,"error: token is empty");
        }
        return success();
    }

    @Override
    public int filterOrder() {
        return 1;
    }
}
