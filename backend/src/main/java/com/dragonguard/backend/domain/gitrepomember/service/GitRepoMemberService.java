package com.dragonguard.backend.domain.gitrepomember.service;

import com.dragonguard.backend.domain.gitrepo.entity.GitRepo;
import com.dragonguard.backend.domain.gitrepo.repository.GitRepoRepository;
import com.dragonguard.backend.domain.gitrepomember.dto.response.GitRepoMemberResponse;
import com.dragonguard.backend.domain.gitrepomember.entity.GitRepoMember;
import com.dragonguard.backend.domain.gitrepomember.mapper.GitRepoMemberMapper;
import com.dragonguard.backend.domain.gitrepomember.repository.GitRepoMemberRepository;
import com.dragonguard.backend.domain.member.dto.request.MemberRequest;
import com.dragonguard.backend.domain.member.entity.AuthStep;
import com.dragonguard.backend.domain.member.entity.Member;
import com.dragonguard.backend.domain.member.entity.Role;
import com.dragonguard.backend.domain.member.service.MemberService;
import com.dragonguard.backend.global.service.EntityLoader;
import com.dragonguard.backend.global.exception.EntityNotFoundException;
import com.dragonguard.backend.global.service.TransactionService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 김승진
 * @description GitRepoMember 관련 서비스로직을 담당하는 클래스
 */

@TransactionService
@RequiredArgsConstructor
public class GitRepoMemberService implements EntityLoader<GitRepoMember, Long> {

    private final GitRepoMemberRepository gitRepoMemberRepository;
    private final MemberService memberService;
    private final GitRepoRepository gitRepoRepository;
    private final GitRepoMemberMapper gitRepoMemberMapper;

    public void updateOrSaveAll(final List<GitRepoMemberResponse> gitRepoResponses, final String gitRepoName) {
        List<GitRepoMember> gitRepoMembers = validateAndGetGitRepoMembers(gitRepoResponses, gitRepoName);
        saveOrUpdateGitRepoMembers(gitRepoMembers);
    }

    private void saveOrUpdateGitRepoMembers(final List<GitRepoMember> gitRepoMembers) {
        gitRepoMembers.forEach(gitRepoMember -> gitRepoMemberRepository.findByGitRepoAndMember(
                        gitRepoMember.getGitRepo(),
                        gitRepoMember.getMember())
                .orElseGet(() -> {
                    GitRepoMember savedGitRepoMember = gitRepoMemberRepository.save(gitRepoMember);
                    return gitRepoMemberRepository.findById(savedGitRepoMember.getId())
                            .orElseThrow(EntityNotFoundException::new);
                })
                .update(gitRepoMember));
    }

    private List<GitRepoMember> validateAndGetGitRepoMembers(final List<GitRepoMemberResponse> gitRepoMemberResponses, final String gitRepoName) {
        return gitRepoMemberResponses.stream()
                .map(gitRepoResponse -> findByNameAndMemberGithubId(gitRepoName, gitRepoResponse.getGithubId()))
                .collect(Collectors.toList());
    }

    public void saveAll(final List<GitRepoMemberResponse> gitRepoResponses, final GitRepo gitRepo) {
        gitRepoMemberRepository.saveAll(getGitRepoMembers(gitRepoResponses, gitRepo));
    }

    private List<GitRepoMember> getGitRepoMembers(final List<GitRepoMemberResponse> gitRepoResponses, final GitRepo gitRepo) {
        return gitRepoResponses.stream()
                .distinct()
                .map(gitRepoResponse ->
                        getGitRepoMember(gitRepoResponse, getMemberByGitRepoResponse(gitRepoResponse),
                                gitRepo))
                .collect(Collectors.toList());
    }

    private GitRepoMember getGitRepoMember(final GitRepoMemberResponse gitRepoMemberResponse, final Member member, final GitRepo gitRepo) {
        GitRepoMember gitRepoMember = gitRepoMemberRepository.findByGitRepoAndMember(gitRepo, member)
                .orElse(gitRepoMemberMapper.toEntity(member, gitRepo));

        gitRepoMember.updateGitRepoContribution(
                gitRepoMemberResponse.getCommits(),
                gitRepoMemberResponse.getAdditions(),
                gitRepoMemberResponse.getDeletions());

        return gitRepoMember;
    }

    private GitRepo getGitRepoByName(final String gitRepoName) {
        return gitRepoRepository.findByName(gitRepoName).orElseThrow(EntityNotFoundException::new);
    }

    private Member getMemberByGitRepoResponse(final GitRepoMemberResponse gitRepository) {
        return memberService.findMemberOrSave(new MemberRequest(gitRepository.getGithubId()), AuthStep.NONE);
    }

    public GitRepoMember findByNameAndMemberGithubId(final String gitRepoName, final String githubId) {
        return gitRepoMemberRepository.findByNameAndMemberGithubId(gitRepoName, githubId)
                .orElseGet(() -> {
                    GitRepoMember gitRepoMember = gitRepoMemberRepository.save(
                            gitRepoMemberMapper.toEntity(
                                    memberService.findMemberOrSaveWithRole(githubId, Role.ROLE_USER, AuthStep.NONE),
                                    getGitRepoByName(gitRepoName)));
                    return gitRepoMemberRepository.findById(gitRepoMember.getId())
                            .orElseThrow(EntityNotFoundException::new);
                });
    }

    @Override
    public GitRepoMember loadEntity(final Long id) {
        return gitRepoMemberRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}
