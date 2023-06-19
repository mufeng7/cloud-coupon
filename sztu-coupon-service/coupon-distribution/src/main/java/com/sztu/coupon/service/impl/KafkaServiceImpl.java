package com.sztu.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.sztu.coupon.constant.Constant;
import com.sztu.coupon.constant.CouponStatus;
import com.sztu.coupon.dao.CouponDao;
import com.sztu.coupon.entity.Coupon;
import com.sztu.coupon.service.IKafkaService;
import com.sztu.coupon.vo.CouponKafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {

    @Autowired
    private CouponDao couponDao;

    @Override
    @KafkaListener(topics = {Constant.TOPIC},groupId = "sztu-coupon-1")
    public void consumeCouponKafkaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()){
            Object message = kafkaMessage.get();
            CouponKafkaMessage couponInfo = JSON.parseObject(message.toString(),CouponKafkaMessage.class);
            log.info("Receive CouponKafkaMessage: {}",message.toString());
            CouponStatus status = CouponStatus.of(couponInfo.getStatus());
            switch (status){
                case USERD:
                    processUsedCoupons(couponInfo,status);
                    break;
                case USABLE:
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo,status);
                    break;
            }
        }
    }

    /**
     * 处理使用过的优惠券
     * @param kafkaMessage
     * @param status
     */
    private void processUsedCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status){
        //TODO 短信推送
        processCouponByStatus(kafkaMessage,status);
    }

    /**
     * 处理过期的优惠券
     * @param kafkaMessage
     * @param status
     */
    private void processExpiredCoupons(CouponKafkaMessage kafkaMessage, CouponStatus status){
        //TODO 消息推送
        processCouponByStatus(kafkaMessage,status);
    }

    private void processCouponByStatus(CouponKafkaMessage kafkaMessage, CouponStatus status) {
        List<Coupon> coupons = couponDao.findAllById(kafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons)){
            log.error("KafkaMessage is NULL: {}", JSON.toJSONString(kafkaMessage));
            return;
        }
        coupons.forEach(
                t -> t.setStatus(status)
        );
        log.info("CouponKafkaMessage Op Coupon Count: {}",couponDao.saveAll(coupons).size());
    }



}
