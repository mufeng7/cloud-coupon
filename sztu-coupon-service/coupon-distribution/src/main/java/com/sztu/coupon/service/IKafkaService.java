package com.sztu.coupon.service;


import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IKafkaService {
    /**
     * ConsumerRecord:读取到对象的java表示
     * @param record
     */
    void consumeCouponKafkaMessage(ConsumerRecord<?,?> record);
}
