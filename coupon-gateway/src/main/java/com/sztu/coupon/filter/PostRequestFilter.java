package com.sztu.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class PostRequestFilter extends AbstractPostZuulFilter{
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        Long startTime = (Long) context.get("startTime");
        String uri = request.getRequestURI();
        Long duration = System.currentTimeMillis() - startTime;
        //从网关通过请求都会打印日志
        log.info("uri: {},duration: {}",uri,duration);
        return success();
    }

    /**
     * 在最后执行
     * @return
     */
    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }
}
