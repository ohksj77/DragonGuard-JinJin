package com.dragonguard.backend.domain.blockchain.mapper;

import com.dragonguard.backend.domain.blockchain.dto.request.ContractRequest;
import com.dragonguard.backend.domain.blockchain.dto.response.BlockchainResponse;
import com.dragonguard.backend.domain.blockchain.entity.Blockchain;
import com.dragonguard.backend.domain.blockchain.entity.ContributeType;
import com.dragonguard.backend.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * @author 김승진
 * @description 블록체인 Entity와 dto의 변환을 돕는 클래스
 */

@Component
public class BlockchainMapper {

    public Blockchain toEntity(final BigInteger amount, final Member member, final ContractRequest request) {
        return Blockchain.builder()
                .contributeType(ContributeType.valueOf(request.getContributeType().toUpperCase()))
                .amount(amount)
                .member(member)
                .build();
    }

    public BlockchainResponse toResponse(final Blockchain blockchain) {
        return BlockchainResponse.builder()
                .id(blockchain.getId())
                .contributeType(blockchain.getContributeType())
                .amount(blockchain.getAmount())
                .githubId(blockchain.getMember().getGithubId())
                .memberId(blockchain.getMember().getId())
                .build();
    }
}
