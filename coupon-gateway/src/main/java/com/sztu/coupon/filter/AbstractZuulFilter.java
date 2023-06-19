package com.sztu.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 通用过滤器类
 */
public abstract class AbstractZuulFilter extends ZuulFilter {

    //数据保存在每个请求的ThreadLocal中，所以是线程安全的
    //扩展concurrentHashmap
    RequestContext context;

    private final static String NEXT = "next";

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (boolean) ctx.getOrDefault(NEXT,true);
    }

    @Override
    public Object run() throws ZuulException {
        context = RequestContext.getCurrentContext();
        return cRun();
    }

    protected abstract Object cRun();

    Object fail(int code,String msg){
        context.set(NEXT,false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(String.format(
                "{\"result\":\"%s!\"}",msg
        ));
        return null;
    }

    Object success(){
        context.set(NEXT,true);
        return null;
    }
}
