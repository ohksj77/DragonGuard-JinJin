package com.dragonguard.backend.domain.search.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author 김승진
 * @description 검색에 대한 Kafka 요청 정보를 담는 dto
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaSearchRequest {
    private String name;
    private String type;
    private Integer page;
}
