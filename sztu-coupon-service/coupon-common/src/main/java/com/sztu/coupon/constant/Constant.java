package com.sztu.coupon.constant;

/**
 * 通用常量定义
 */
public class Constant {
    //消息topic
    public static final String TOPIC = "sztu_user_coupon_op";

    //
    public static class RedisPrefix{
        //优惠券码key前缀
        public static final String COUPON_TEMPLATE = "sztu_coupon_template_code_";

        //所有可用的
        public static final String USER_COUPON_USABLE = "sztu_user_coupon_usable_";

        //已经使用的
        public static final String USER_COUPON_USED = "sztu_user_coupon_used_";

        //过期的
        public static final String USER_COUPON_EXPIRED = "sztu_user_coupon_expired_";
    }
}
