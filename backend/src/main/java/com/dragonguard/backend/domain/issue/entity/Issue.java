package com.dragonguard.backend.domain.issue.entity;

import com.dragonguard.backend.domain.member.entity.Member;
import com.dragonguard.backend.global.audit.Auditable;
import com.dragonguard.backend.global.audit.BaseTime;
import com.dragonguard.backend.global.audit.SoftDelete;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author 김승진
 * @description issue 정보를 담는 DB Entity
 */

@Getter
@Entity
@SoftDelete
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Issue implements Auditable {

    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(columnDefinition = "BINARY(16)")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Member member;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Integer year;

    @Setter
    @Embedded
    @Column(nullable = false)
    private BaseTime baseTime;

    @Builder
    public Issue(final Member member, final Integer amount, final Integer year) {
        if (amount < 0) return;
        this.member = member;
        this.amount = amount;
        this.year = year;
        organizeMember(member);
    }

    public void updateIssueNum(final Integer amount) {
        this.amount = amount;
    }

    public boolean customEqualsWithAmount(final Issue issue) {
        return member.getGithubId().equals(issue.member.getGithubId()) && amount.intValue() == issue.amount.intValue() && year.intValue() == issue.year.intValue();
    }

    private void organizeMember(final Member member) {
        member.addIssue(this);
    }

    public boolean isNotUpdatable(final Integer amount) {
        return Optional.ofNullable(this.baseTime.getUpdatedAt()).orElseGet(() -> this.baseTime.getCreatedAt()).isAfter(LocalDateTime.now().minusSeconds(20L))
                || this.amount.intValue() == amount.intValue();
    }
}
