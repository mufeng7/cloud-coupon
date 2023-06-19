package com.sztu.coupon.converter;

import com.sztu.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

/**
 * 优惠券分类枚举属性转换器
 * AttributeConverter<X,Y> X:是实体数据类型，Y：数据库字段类型
 */
@Convert
public class CouponCategoryConverter implements AttributeConverter<CouponCategory,String> {

    /**
     * 将实体数据X转换位Y存储道数据库中
     * @param couponCategory
     * @return
     */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    @Override
    public CouponCategory convertToEntityAttribute(String s) {
        return CouponCategory.of(s);
    }
}
