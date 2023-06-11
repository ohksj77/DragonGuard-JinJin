package com.dragonguard.backend.domain.gitrepomember.repository;

import com.dragonguard.backend.domain.gitrepo.entity.GitRepo;
import com.dragonguard.backend.domain.gitrepomember.entity.GitRepoContribution;
import com.dragonguard.backend.domain.gitrepomember.entity.GitRepoMember;
import com.dragonguard.backend.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

/**
 * @author 김승진
 * @description 깃허브 Repository 기여자들의 정보들에 대한 DB 조회 접근에 대한 인터페이스
 */

public interface GitRepoMemberQueryRepository {
    List<GitRepoMember> findAllByGitRepo(GitRepo gitRepo);
    boolean existsByGitRepoAndMember(GitRepo gitRepo, Member member);
    boolean existsByGitRepoAndMemberAndGitRepoContribution(GitRepo gitRepo, Member member, GitRepoContribution gitRepoContribution);
    Optional<GitRepoMember> findByGitRepoAndMember(GitRepo gitRepo, Member member);
    Optional<GitRepoMember> findByNameAndMemberName(String name, String githubId);
    Optional<GitRepoMember> findById(Long id);
}
