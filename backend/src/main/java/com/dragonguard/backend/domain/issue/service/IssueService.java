package com.dragonguard.backend.domain.issue.service;

import com.dragonguard.backend.domain.blockchain.dto.kafka.BlockchainKafkaRequest;
import com.dragonguard.backend.domain.blockchain.service.BlockchainService;
import com.dragonguard.backend.domain.issue.entity.Issue;
import com.dragonguard.backend.global.template.kafka.KafkaProducer;
import com.dragonguard.backend.global.template.mapper.ContributionMapper;
import com.dragonguard.backend.global.template.repository.ContributionRepository;
import com.dragonguard.backend.global.template.service.ContributionService;
import com.dragonguard.backend.global.annotation.TransactionService;

/**
 * @author 김승진
 * @description issue Entity의 서비스 로직을 담당하는 클래스
 */

@TransactionService
public class IssueService extends ContributionService<Issue, Long> {

    public IssueService(
            final ContributionRepository<Issue, Long> contributionRepository,
            final ContributionMapper<Issue> commitMapper,
            final KafkaProducer<BlockchainKafkaRequest> blockchainKafkaProducer,
            final BlockchainService blockchainService) {
        super(contributionRepository, commitMapper, blockchainKafkaProducer, blockchainService);
    }
}
