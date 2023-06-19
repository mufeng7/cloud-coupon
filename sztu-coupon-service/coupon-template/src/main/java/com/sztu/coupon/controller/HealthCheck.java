package com.sztu.coupon.controller;

import com.sztu.coupon.exception.CouponException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class HealthCheck {

    private final DiscoveryClient client;

    private final Registration registration;

    public HealthCheck(DiscoveryClient client, Registration registration) {
        this.client = client;
        this.registration = registration;
    }

    @GetMapping("/health")
    public String health(){
        log.debug("view health api");
        return "view is OK";
    }

    @GetMapping("/exception")
    public String exception() throws CouponException{
        log.debug("view exception api");
        throw new CouponException("Sname Problem");
    }

    /**
     * 获取Eureka Server上微服务的远信息
     * @return
     */
    @GetMapping("/info")
    public List<Map<String,Object>> info(){
        //大概要等两分钟才能获取到注册信息
        List<ServiceInstance> instances = client.getInstances(registration.getServiceId());
        List<Map<String,Object>> result = new ArrayList<>(instances.size());
        instances.forEach(
                i ->{
                    Map<String,Object> info = new HashMap<>();
                    info.put("serviceId",i.getServiceId());
                    info.put("instanceId",i.getInstanceId());
                    info.put("port",i.getPort());
                    result.add(info);
                }
        );
        return result;
    }


}
