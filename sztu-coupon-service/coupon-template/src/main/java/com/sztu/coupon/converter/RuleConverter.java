package com.sztu.coupon.converter;

import com.alibaba.fastjson.JSON;
import com.sztu.coupon.vo.TemplateRule;

import javax.persistence.AttributeConverter;

/**
 * 规则转换器
 */
public class RuleConverter implements AttributeConverter<TemplateRule,String> {
    @Override
    public String convertToDatabaseColumn(TemplateRule templateRule) {
        return JSON.toJSONString(templateRule);
    }

    @Override
    public TemplateRule convertToEntityAttribute(String s) {
        return JSON.parseObject(s,TemplateRule.class);
    }
}
