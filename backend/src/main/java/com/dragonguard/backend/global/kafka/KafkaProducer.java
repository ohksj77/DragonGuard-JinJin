package com.dragonguard.backend.global.kafka;

/**
 * @author 김승진
 * @description Kafka Producer 의 공통 기능을 뽑아낸 제네릭 인터페이스
 */

public interface KafkaProducer<T> {
    void send(final T request);
}
