package com.dragonguard.backend.domain.member.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaRepositoryRequest {
    private String githubId;
    private String githubToken;
}
