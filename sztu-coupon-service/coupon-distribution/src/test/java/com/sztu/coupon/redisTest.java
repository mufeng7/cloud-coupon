package com.sztu.coupon;

import com.sztu.coupon.entity.Coupon;
import com.sztu.coupon.service.IRedisServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class redisTest {

    @Autowired
    private IRedisServer redisServer;

    @Test
    public void testgetValue(){
        System.out.println("-------------------------");
        List<Coupon> cachedCoupons = redisServer.getCachedCoupons(1L, 2);
        System.out.println(cachedCoupons.toString());
        String s = redisServer.tryToAcquireCouponFromCache(13);
        System.out.println(s);
        System.out.println("---------------------------------");
    }
}
