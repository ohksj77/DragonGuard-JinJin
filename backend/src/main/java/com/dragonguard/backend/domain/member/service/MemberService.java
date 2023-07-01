package com.dragonguard.backend.domain.member.service;

import com.dragonguard.backend.domain.blockchain.service.BlockchainService;
import com.dragonguard.backend.domain.commit.entity.Commit;
import com.dragonguard.backend.domain.contribution.dto.kafka.ContributionKafkaResponse;
import com.dragonguard.backend.domain.contribution.service.ContributionService;
import com.dragonguard.backend.domain.gitorganization.service.GitOrganizationService;
import com.dragonguard.backend.domain.gitrepo.repository.GitRepoRepository;
import com.dragonguard.backend.domain.issue.entity.Issue;
import com.dragonguard.backend.domain.member.dto.kafka.KafkaContributionRequest;
import com.dragonguard.backend.domain.member.dto.kafka.KafkaRepositoryRequest;
import com.dragonguard.backend.domain.member.dto.request.MemberRequest;
import com.dragonguard.backend.domain.member.dto.request.WalletRequest;
import com.dragonguard.backend.domain.member.dto.response.*;
import com.dragonguard.backend.domain.member.entity.AuthStep;
import com.dragonguard.backend.domain.member.entity.Member;
import com.dragonguard.backend.domain.member.entity.Role;
import com.dragonguard.backend.domain.member.entity.Tier;
import com.dragonguard.backend.domain.member.mapper.MemberMapper;
import com.dragonguard.backend.domain.member.repository.MemberRepository;
import com.dragonguard.backend.domain.organization.repository.OrganizationRepository;
import com.dragonguard.backend.domain.pullrequest.entity.PullRequest;
import com.dragonguard.backend.global.IdResponse;
import com.dragonguard.backend.global.exception.EntityNotFoundException;
import com.dragonguard.backend.global.kafka.KafkaProducer;
import com.dragonguard.backend.global.service.EntityLoader;
import com.dragonguard.backend.global.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author 김승진
 * @description 멤버관련 서비스 로직을 담당하는 클래스
 */

@TransactionService
@RequiredArgsConstructor
public class MemberService implements EntityLoader<Member, UUID> {
    private final MemberRepository memberRepository;
    private final MemberClientService memberClientService;
    private final MemberMapper memberMapper;
    private final ContributionService contributionService;
    private final BlockchainService blockchainService;
    private final AuthService authService;
    private final OrganizationRepository organizationRepository;
    private final GitOrganizationService gitOrganizationService;
    private final GitRepoRepository gitRepoRepository;
    private final KafkaProducer<KafkaRepositoryRequest> kafkaRepositoryProducer;
    private final KafkaProducer<KafkaContributionRequest> kafkaContributionClientProducer;

    public Tier getTier() {
        Member member = getLoginUserWithPersistence();
        member.updateTier();
        return member.getTier();
    }

    public IdResponse<UUID> saveMember(final MemberRequest memberRequest, final Role role) {
        return new IdResponse<>(scrapeAndGetSavedMember(memberRequest.getGithubId(), role, AuthStep.GITHUB_ONLY).getId());
    }

    private Member saveAndRequestClient(final String githubId, final Role role, final AuthStep authStep) {
        Member member = findMemberOrSaveWithRole(githubId, role, authStep);
        sendGitRepoAndContributionRequestToKafka(githubId, member.getGithubToken());
        return member;
    }

    public Member findMemberOrSaveWithRole(final String githubId, final Role role, final AuthStep authStep) {
        if (memberRepository.existsByGithubId(githubId)) {
            return getMemberByGithubId(githubId);
        }
        Member member = memberRepository.save(memberMapper.toEntity(githubId, role, authStep));
        return loadEntity(member.getId());
    }

    public Member findMemberOrSave(final MemberRequest memberRequest, final AuthStep authStep, String profileUrl) {
        if (memberRepository.existsByGithubId(memberRequest.getGithubId())) {
            return getMemberByGithubId(memberRequest.getGithubId());
        }
        Member member = memberRepository.save(memberMapper.toEntity(memberRequest, authStep, profileUrl));
        return loadEntity(member.getId());
    }

    public void addMemberContributionsAndUpdate(final ContributionKafkaResponse contributionKafkaResponse) {
        Member member = findMemberAndUpdate(contributionKafkaResponse);

        if (addContributionsIfNotEmpty(member, contributionKafkaResponse.getContribution())) return;

        sendTransactionIfWalletAddressExists(member);
    }

    private Member findMemberAndUpdate(final ContributionKafkaResponse contributionKafkaResponse) {
        Member member = findByGithubIdOrSaveWithAuthStep(contributionKafkaResponse.getGithubId());
        member.updateNameAndImage(contributionKafkaResponse.getName(), contributionKafkaResponse.getProfileImage());
        return member;
    }

    private void sendTransactionIfWalletAddressExists(final Member member) {
        if (member.validateWalletAddressAndUpdateTier()) return;
        transactionAndUpdateTier(member);
    }

    private boolean addContributionsIfNotEmpty(
            final Member member,
            final Integer contribution) {
        List<Commit> commits = member.getCommits();
        List<PullRequest> pullRequests = member.getPullRequests();
        List<Issue> issues = member.getIssues();

        if (isContributionEmpty(commits, pullRequests, issues, contribution, member.getSumOfTokens())) return true;
        member.updateSumOfReviewsWithCalculation(contribution);
        return false;
    }

    public void updateContributions() {
        Member member = getLoginUserWithPersistence();
        sendGitRepoAndContributionRequestToKafka(member.getGithubId(), member.getGithubToken());

        if (member.isWalletAddressExists()) transactionAndUpdateTier(member);
    }

    public MemberResponse getMember() {
        Member member = getLoginUserWithPersistence();
        String githubId = member.getGithubId();
        sendGitRepoAndContributionRequestToKafka(githubId, member.getGithubToken());
        return getMemberResponseWithValidateOrganization(member);
    }

    private MemberResponse getMemberResponseWithValidateOrganization(final Member member) {
        member.validateWalletAddressAndUpdateTier();
        if (hasNoOrganization(member)) {
            return memberMapper.toResponse(member, memberRepository.findRankingById(member.getId()));
        }
        return getMemberResponse(member);
    }

    private MemberResponse getMemberResponse(final Member member) {
        UUID memberId = member.getId();

        return memberMapper.toResponse(
                member,
                memberRepository.findRankingById(memberId),
                member.getOrganization().getName(),
                organizationRepository.findRankingByMemberId(memberId));
    }

    private Member findByGithubIdOrSaveWithAuthStep(final String githubId) {
        return memberRepository.findByGithubId(githubId)
                .orElse(scrapeAndGetSavedMember(githubId, Role.ROLE_USER, AuthStep.NONE));
    }

    @Transactional(readOnly = true)
    public List<MemberRankResponse> getMemberRanking(final Pageable pageable) {
        return memberRepository.findRanking(pageable);
    }

    public void updateWalletAddress(final WalletRequest walletRequest) {
        Member member = getLoginUserWithPersistence();
        member.updateWalletAddress(walletRequest.getWalletAddress());
        sendGitRepoAndContributionRequestToKafka(member.getGithubId(), member.getGithubToken());
    }

    private void sendContributionRequestToKafka(final String githubId) {
        kafkaContributionClientProducer.send(new KafkaContributionRequest(githubId));
    }


    @Transactional(readOnly = true)
    public List<MemberRankResponse> getMemberRankingByOrganization(final Long organizationId, final Pageable pageable) {
        return memberRepository.findRankingByOrganization(organizationId, pageable);
    }

    @Override
    public Member loadEntity(final UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Member getLoginUserWithPersistence() {
        return loadEntity(authService.getLoginUserId());
    }

    private Member scrapeAndGetSavedMember(final String githubId, final Role role, final AuthStep authStep) {
        Member member = saveAndRequestClient(githubId, role, authStep);
        sendGitRepoAndContributionRequestToKafka(githubId, member.getGithubToken());
        return member;
    }

    public MemberGitReposAndGitOrganizationsResponse findMemberDetails() {
        Member member = getLoginUserWithPersistence();
        String githubId = member.getGithubId();
        sendGitRepoAndContributionRequestToKafka(githubId, member.getGithubToken());

        return getMemberGitReposAndGitOrganizations(githubId, member);
    }

    public void updateBlockchain() {
        transactionAndUpdateTier(getLoginUserWithPersistence());
    }

    private void transactionAndUpdateTier(final Member member) {
        blockchainService.sendSmartContractTransaction(member);
        member.validateWalletAddressAndUpdateTier();
    }

    public void updateContributionAndTransaction(final Member member) {
        transactionAndUpdateTier(member);
        memberClientService.addMemberContribution(member);
    }

    private void getContributionSumByScraping(final String githubId) {
        contributionService.scrapingCommits(githubId);
    }

    private MemberGitReposAndGitOrganizationsResponse getMemberGitReposAndGitOrganizations(final String githubId, final Member member) {
        return memberMapper.toRepoAndOrgResponse(
                member.getProfileImage(),
                gitOrganizationService.findGitOrganizationByMember(member),
                gitRepoRepository.findByGithubId(githubId));
    }

    private boolean hasNoOrganization(final Member member) {
        return member.getOrganization() == null;
    }

    private void sendGitRepoAndContributionRequestToKafka(final String githubId, final String githubToken) {
        sendGitRepoRequestToKafka(githubId, githubToken);
        sendContributionRequestToKafka(githubId);
        getContributionSumByScraping(githubId);
    }

    private void sendGitRepoRequestToKafka(final String githubId, final String githubToken) {
        kafkaRepositoryProducer.send(new KafkaRepositoryRequest(githubId, githubToken));
    }

    private boolean isContributionEmpty(final List<Commit> commits, final List<PullRequest> pullRequests, final List<Issue> issues, final Integer contribution, final Long tokenSum) {
        return commits.isEmpty() || pullRequests.isEmpty() || issues.isEmpty() || contribution.longValue() == tokenSum;
    }

    public MemberGitOrganizationRepoResponse getMemberGitOrganizationRepo(final String gitOrganizationName) {
        return new MemberGitOrganizationRepoResponse(
                gitOrganizationService.findByName(gitOrganizationName).getProfileImage(),
                memberClientService.requestGitOrganizationResponse(getLoginUserWithPersistence().getGithubToken(), gitOrganizationName));
    }

    public MemberDetailsResponse getMemberDetails(final String githubId) {
        Member member = getMemberByGithubId(githubId);
        Integer rank = memberRepository.findRankingById(member.getId());
        return memberMapper.toDetailsResponse(member, rank);
    }

    private Member getMemberByGithubId(final String githubId) {
        return memberRepository.findByGithubId(githubId)
                .orElseThrow(EntityNotFoundException::new);
    }
}
