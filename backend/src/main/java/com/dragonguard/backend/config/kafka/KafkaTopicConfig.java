package com.dragonguard.backend.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


/**
 * @author 김승진
 * @description Kafka의 토픽을 생성해주는 클래스
 */

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic commitTopic() {
        return TopicBuilder
                .name("gitrank.to.scrape.contribution")
                .build();
    }

    @Bean
    public NewTopic resultTopic() {
        return TopicBuilder
                .name("gitrank.to.scrape.result")
                .build();
    }

    @Bean
    public NewTopic gitRepoTopic() {
        return TopicBuilder
                .name("gitrank.to.scrape.git-repos")
                .build();
    }

    @Bean
    public NewTopic issueTopic() {
        return TopicBuilder
                .name("gitrank.to.scrape.issues")
                .build();
    }

    @Bean
    public NewTopic contributionTopic() {
        return TopicBuilder
                .name("gitrank.to.backend.contribution")
                .build();
    }

    @Bean
    public NewTopic repositoryTopic() {
        return TopicBuilder
                .name("gitrank.to.backend.repository")
                .build();
    }
}
