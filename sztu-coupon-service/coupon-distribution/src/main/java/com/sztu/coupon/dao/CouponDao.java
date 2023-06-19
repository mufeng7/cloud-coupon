package com.sztu.coupon.dao;

import com.sztu.coupon.constant.CouponStatus;
import com.sztu.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponDao extends JpaRepository<Coupon,Integer> {

    List<Coupon> findAllByUserIdAndStatus(Long userId, CouponStatus status);

}
