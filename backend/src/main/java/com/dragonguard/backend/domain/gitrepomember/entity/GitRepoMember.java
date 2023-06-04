package com.dragonguard.backend.domain.gitrepomember.entity;

import com.dragonguard.backend.domain.gitrepo.entity.GitRepo;
import com.dragonguard.backend.global.audit.BaseTime;
import com.dragonguard.backend.global.SoftDelete;
import com.dragonguard.backend.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author 김승진
 * @description 깃허브 Repository 구성원 정보를 담는 DB Entity
 */

@Getter
@Entity
@SoftDelete
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GitRepoMember extends BaseTime {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "git_repo_id")
    private GitRepo gitRepo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded
    private GitRepoContribution gitRepoContribution;

    @Builder
    public GitRepoMember(GitRepo gitRepo, Member member, GitRepoContribution gitRepoContribution) {
        this.gitRepo = gitRepo;
        this.member = member;
        this.gitRepoContribution = gitRepoContribution;
    }

    public void update(GitRepoMember gitRepoMember) {
        this.gitRepo = gitRepoMember.gitRepo;
        this.member = gitRepoMember.member;
        this.gitRepoContribution = gitRepoMember.gitRepoContribution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitRepoMember that = (GitRepoMember) o;
        return Objects.equals(gitRepo, that.gitRepo) && Objects.equals(member, that.member) && Objects.equals(gitRepoContribution, that.gitRepoContribution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gitRepo, member, gitRepoContribution);
    }
}