package com.dragonguard.backend.member.dto.request.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaContributionRequest {
    private String githubId;
}
