package com.sztu.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sztu.coupon.constant.CouponStatus;
import com.sztu.coupon.converter.CouponStatusConverter;
import com.sztu.coupon.serialization.CouponSerialize;
import com.sztu.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;


/**
 * 优惠券（用户领取的优惠券记录）
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 优惠券模板的主键（逻辑外键）
     */
    @Column(name = "template_id",nullable = false)
    private Integer templateId;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(name = "coupon_code",nullable = false)
    private String couponCode;

    @CreatedDate
    @Column(name = "assign_time",nullable = false)
    private Date assignTime;

    @Column(name = "status",nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;

    @Transient
    private CouponTemplateSDK templateSDK;

    /**
     * 返回一个无效的Coupon对象
      * @return
     */
    public static Coupon invalidCoupon(){
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    public Coupon(Integer templateId,Long userId,String couponCode,CouponStatus status){
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status;
    }
}
