package com.sztu.coupon.converter;

import com.sztu.coupon.constant.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class CouponStatusConverter implements AttributeConverter<CouponStatus,Integer> {

    /**
     * 将实体熟悉X转换成Y存到数据库中
     * @param status
     * @return
     */
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}
