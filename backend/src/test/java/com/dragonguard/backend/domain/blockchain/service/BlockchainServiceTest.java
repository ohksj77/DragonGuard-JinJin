package com.dragonguard.backend.domain.blockchain.service;

import com.dragonguard.backend.domain.blockchain.dto.request.ContractRequest;
import com.dragonguard.backend.domain.blockchain.dto.response.BlockchainResponse;
import com.dragonguard.backend.domain.blockchain.entity.Blockchain;
import com.dragonguard.backend.domain.blockchain.entity.ContributeType;
import com.dragonguard.backend.domain.blockchain.mapper.BlockchainMapper;
import com.dragonguard.backend.domain.blockchain.repository.BlockchainRepository;
import com.dragonguard.backend.domain.member.repository.MemberRepository;
import com.dragonguard.backend.support.database.DatabaseTest;
import com.dragonguard.backend.support.database.LoginTest;
import com.dragonguard.backend.support.fixture.blockchain.entity.BlockchainFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DatabaseTest
@DisplayName("admin 서비스의")
@DependsOn("blockchainDeploy")
class BlockchainServiceTest extends LoginTest {

    @Autowired private BlockchainService blockchainService;
    @Autowired private BlockchainRepository blockchainRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private BlockchainMapper blockchainMapper;
    @Value("${wallet}") private String walletAddress;

    @Test
    @DisplayName("블록체인 트랜잭션 수행")
    void setTransaction() {
        //given
        memberRepository.findById(loginUser.getId()).ifPresent(m -> m.updateWalletAddress(walletAddress));

        //when
        blockchainService.setTransaction(new ContractRequest(ContributeType.COMMIT.toString(), BigInteger.ONE), loginUser);
        List<Blockchain> blockchains = blockchainRepository.findAllByMemberId(loginUser.getId());

        //then
        assertThat(blockchains).hasSize(1);
        assertThat(blockchains.get(0).getMember()).isEqualTo(loginUser);
        assertThat(blockchains.get(0).getContributeType()).isEqualTo(ContributeType.COMMIT);
    }

    @Test
    @DisplayName("블록체인 내역 조회")
    void getBlockchainList() {
        //given
        List<Blockchain> given = List.of(BlockchainFixture.SAMPLE1.toEntity(loginUser),
                BlockchainFixture.SAMPLE1.toEntity(loginUser),
                BlockchainFixture.SAMPLE2.toEntity(loginUser));
        given.forEach(blockchainRepository::save);

        List<BlockchainResponse> expected = given.stream().map(blockchainMapper::toResponse).collect(Collectors.toList());

        //when
        List<BlockchainResponse> blockchains = blockchainService.getBlockchainList();

        //then
        assertThat(blockchains).hasSize(3);
        assertThat(blockchains.get(0).getMemberId()).isEqualTo(loginUser.getId());
        assertThat(blockchains.get(0).getId()).isEqualTo(expected.get(0).getId());
        assertThat(blockchains.get(1).getId()).isEqualTo(expected.get(1).getId());
        assertThat(blockchains.get(2).getId()).isEqualTo(expected.get(2).getId());
    }

    @Test
    @DisplayName("엔티티 id로 조회")
    void loadEntity() {
        //given
        Blockchain expected = blockchainRepository.save(BlockchainFixture.SAMPLE1.toEntity(loginUser));

        //when
        Blockchain result = blockchainService.loadEntity(expected.getId());

        //then
        assertThat(expected).isEqualTo(result);
    }
}