package com.sztu.coupon.vo;

import com.sztu.coupon.constant.CouponCategory;
import com.sztu.coupon.constant.DistributeTarget;
import com.sztu.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 优惠券模板创建请求
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequest {

    private String name;

    private String logo;

    private String desc;

    private String category;

    private Integer productLine;

    private Integer count;

    private Long userId;

    private Integer target;

    private TemplateRule rule;

    public boolean validate(){
        boolean stringValid = StringUtils.isNotEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);

        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);

        boolean numValid = count > 0 && userId > 0 && rule.validate();
        return stringValid && enumValid && numValid;
    }
}
