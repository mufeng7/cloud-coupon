package com.sztu.coupon.vo;

import com.sztu.coupon.constant.CouponStatus;
import com.sztu.coupon.constant.PeriodType;
import com.sztu.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 根据优惠券状态，对优惠券进行分类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponClassify {
    //可以使用的
    private List<Coupon> usable;

    private List<Coupon> used;

    private List<Coupon> expired;

    /**
     * 对当前优惠券进行分类
     * @param coupons
     * @return
     */
    public static CouponClassify classify(List<Coupon> coupons){
        List<Coupon> usable = new ArrayList<>(coupons.size());
        List<Coupon> used = new ArrayList<>(coupons.size());
        List<Coupon> expired = new ArrayList<>(coupons.size());
        coupons.forEach(
                c ->{
                    boolean isTimeExpire;
                    long curTime = new Date().getTime();
                    if (c.getTemplateSDK().getRule().getExpiration().getPeriod().equals(PeriodType.REGULAR.getCode())){
                        isTimeExpire = c.getTemplateSDK().getRule().getExpiration().getDeadline() <= curTime;
                    }else {
                        isTimeExpire = DateUtils.addDays(c.getAssignTime(),c.getTemplateSDK().getRule().getExpiration().getGap()).getTime() <= curTime;
                    }
                    if (c.getStatus() == CouponStatus.USERD){
                        used.add(c);
                    }else if (c.getStatus() == CouponStatus.EXPIRED || isTimeExpire){
                        expired.add(c);
                    }else {
                        usable.add(c);
                    }
                }
        );
        return new CouponClassify(usable,used,expired);
    }

}
