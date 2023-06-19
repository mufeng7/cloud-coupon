package com.sztu.coupon.converter;

import com.sztu.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class ProductLineConverter implements AttributeConverter<ProductLine,Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer code) {
        return ProductLine.of(code);
    }
}
