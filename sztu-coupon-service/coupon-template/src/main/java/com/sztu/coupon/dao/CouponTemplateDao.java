package com.sztu.coupon.dao;

import com.sztu.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponTemplateDao extends JpaRepository<CouponTemplate,Integer> {
    //根据模板名字查询模板
    CouponTemplate findByName(String name);

    //available,expired查询
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available,Boolean expired);

    //
    List<CouponTemplate> findAllByExpired(Boolean expired);
}
