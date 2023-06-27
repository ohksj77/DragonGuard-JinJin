package com.dragonguard.backend.domain.blockchain.entity;

import com.dragonguard.backend.domain.member.entity.Member;
import com.dragonguard.backend.global.audit.AuditListener;
import com.dragonguard.backend.global.audit.Auditable;
import com.dragonguard.backend.global.audit.BaseTime;
import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;

/**
 * @author 김승진
 * @description 블록체인 정보를 담는 DB Entity
 */

@Getter
@Entity
@EntityListeners(AuditListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blockchain implements Auditable {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContributeType contributeType;

    private BigInteger amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(columnDefinition = "BINARY(16)")
    private Member member;

    private String address;

    private String transactionHash;

    @Setter
    @Embedded
    @Column(nullable = false)
    private BaseTime baseTime;

    @Builder
    public Blockchain(ContributeType contributeType, BigInteger amount, Member member, String transactionHash) {
        this.contributeType = contributeType;
        this.amount = amount;
        this.member = member;
        this.address = member.getWalletAddress();
        this.transactionHash = transactionHash;
        organize();
    }

    public String getTransactionHashUrl() {
        return "https://baobab.scope.klaytn.com/tx/" + this.transactionHash + "?tabId=tokenTransfer";
    }

    private void organize() {
        this.member.organizeBlockchain(this);
    }
}
