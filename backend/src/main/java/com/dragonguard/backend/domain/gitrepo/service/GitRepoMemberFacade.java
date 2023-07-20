package com.dragonguard.backend.domain.gitrepo.service;

import com.dragonguard.backend.domain.gitrepo.dto.client.Author;
import com.dragonguard.backend.domain.gitrepo.dto.client.GitRepoMemberClientResponse;
import com.dragonguard.backend.domain.gitrepo.dto.client.Week;
import com.dragonguard.backend.domain.gitrepo.dto.collection.GitRepoContributionMap;
import com.dragonguard.backend.domain.gitrepo.dto.request.GitRepoCompareRequest;
import com.dragonguard.backend.domain.gitrepo.dto.request.GitRepoInfoRequest;
import com.dragonguard.backend.domain.gitrepo.dto.request.GitRepoMemberCompareRequest;
import com.dragonguard.backend.domain.gitrepo.dto.response.*;
import com.dragonguard.backend.domain.gitrepo.entity.GitRepo;
import com.dragonguard.backend.domain.gitrepomember.entity.GitRepoMember;
import com.dragonguard.backend.domain.gitrepomember.mapper.GitRepoMemberMapper;
import com.dragonguard.backend.domain.gitrepomember.service.GitRepoMemberService;
import com.dragonguard.backend.domain.gitrepomember.service.GitRepoMemberServiceImpl;
import com.dragonguard.backend.domain.member.service.AuthService;
import com.dragonguard.backend.global.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@TransactionService
@RequiredArgsConstructor
public class GitRepoMemberFacade implements GitRepoService, GitRepoMemberService {
    private final GitRepoServiceImpl gitRepoServiceImpl;
    private final GitRepoMemberServiceImpl gitRepoMemberServiceImpl;
    private final AuthService authService;
    private final GitRepoMemberMapper gitRepoMemberMapper;

    @Override
    public TwoGitRepoResponse findTwoGitRepos(final GitRepoCompareRequest request) {
        return gitRepoServiceImpl.findTwoGitRepos(request);
    }

    @Override
    public TwoGitRepoResponse findTwoGitReposAndUpdate(final GitRepoCompareRequest request) {
        return gitRepoServiceImpl.findTwoGitReposAndUpdate(request);
    }

    @Override
    public GitRepo getEntityByName(final String name) {
        return gitRepoServiceImpl.findGitRepo(name);
    }

    public GitRepoMemberCompareResponse findTwoGitRepoMember(final GitRepoMemberCompareRequest request) {
        GitRepoMember firstGitRepoMember =
                gitRepoMemberServiceImpl.findByGitRepoAndMemberGithubId(gitRepoServiceImpl.findGitRepo(request.getFirstRepo()), request.getFirstGithubId());

        GitRepoMember secondGitRepoMember =
                gitRepoMemberServiceImpl.findByGitRepoAndMemberGithubId(gitRepoServiceImpl.findGitRepo(request.getSecondRepo()), request.getSecondGithubId());

        return new GitRepoMemberCompareResponse(
                gitRepoMemberMapper.toResponse(firstGitRepoMember),
                gitRepoMemberMapper.toResponse(secondGitRepoMember));
    }

    public List<GitRepoMemberResponse> requestToGithub(final GitRepoInfoRequest gitRepoInfoRequest, final GitRepo gitRepo) {
        Optional<List<GitRepoMemberClientResponse>> responses = gitRepoServiceImpl.requestClientGitRepoMember(gitRepoInfoRequest);
        if (responses.isEmpty()) {
            return List.of();
        }
        Set<GitRepoMemberClientResponse> contributions = new HashSet<>(responses.get());
        if (contributions.isEmpty() || contributions.stream().anyMatch(c -> !StringUtils.hasText(c.getAuthor().getAvatarUrl()))) {
            return List.of();
        }

        List<GitRepoMemberResponse> result = getResponseList(
                contributions,
                gitRepoServiceImpl.getContributionMap(contributions, Week::getA),
                gitRepoServiceImpl.getContributionMap(contributions, Week::getD));

        gitRepoMemberServiceImpl.saveAll(result, gitRepo);
        return result;
    }

    private List<GitRepoMemberResponse> getResponseList(
            final Set<GitRepoMemberClientResponse> contributions,
            final GitRepoContributionMap additions,
            final GitRepoContributionMap deletions) {

        return contributions.stream()
                .filter(c -> c.getWeeks() != null && !c.getWeeks().isEmpty() && c.getTotal() != null && c.getAuthor() != null)
                .map(clientResponse -> {
                    Author author = clientResponse.getAuthor();
                    String githubId = author.getLogin();
                    return new GitRepoMemberResponse(
                            githubId,
                            author.getAvatarUrl(),
                            clientResponse.getTotal(),
                            additions.getContributionByKey(clientResponse),
                            deletions.getContributionByKey(clientResponse),
                            gitRepoMemberServiceImpl.isServiceMember(githubId));
                }).collect(Collectors.toList());
    }

    private List<GitRepoMemberResponse> findMembersByGitRepoWithClient(final GitRepoInfoRequest gitRepoInfoRequest) {
        GitRepo gitRepo = gitRepoServiceImpl.findGitRepo(gitRepoInfoRequest.getName());
        return requestToGithub(gitRepoInfoRequest, gitRepo);
    }

    private List<GitRepoMemberResponse> getGitRepoMemberResponses(final String repo, final Integer year, final String githubToken) {
        return findMembersByGitRepoWithClient(new GitRepoInfoRequest(githubToken, repo, year));
    }

    private TwoGitRepoMemberResponse getTwoGitRepoMemberResponse(final GitRepoCompareRequest gitRepoCompareRequest, final Integer year, final String githubToken) {
        return new TwoGitRepoMemberResponse(getGitRepoMemberResponses(gitRepoCompareRequest.getFirstRepo(), year, githubToken),
                getGitRepoMemberResponses(gitRepoCompareRequest.getSecondRepo(), year, githubToken));
    }

    public TwoGitRepoMemberResponse findMembersByGitRepoForCompareAndUpdate(final GitRepoCompareRequest gitRepoCompareRequest) {
        return getTwoGitRepoMemberResponse(gitRepoCompareRequest,
                LocalDate.now().getYear(), authService.getLoginUser().getGithubToken());
    }

    private List<GitRepoMemberResponse> getGitRepoMemberResponses(final GitRepo gitRepo, final String githubToken) {
        Set<GitRepoMember> gitRepoMembers = gitRepo.getGitRepoMembers();
        if (isGitRepoMemberValid(gitRepoMembers)) {
            gitRepoServiceImpl.requestKafkaGitRepoInfo(githubToken, gitRepo.getName());
            return gitRepoMemberMapper.toResponseList(gitRepoMembers);
        }
        return requestToGithub(new GitRepoInfoRequest(githubToken, gitRepo.getName(), LocalDate.now().getYear()), gitRepo);
    }

    private boolean isGitRepoMemberValid(final Set<GitRepoMember> gitRepoMembers) {
        return !gitRepoMembers.isEmpty() && gitRepoMembers.stream().noneMatch(grm ->
                Objects.isNull(grm.getGitRepoContribution()) || StringUtils.hasText(grm.getMember().getProfileImage()));
    }

    public GitRepoResponse findGitRepoInfos(final String name) {
        String githubToken = authService.getLoginUser().getGithubToken();
        GitRepo gitRepo = gitRepoServiceImpl.findGitRepo(name);

        return new GitRepoResponse(
                gitRepoServiceImpl.updateAndGetSparkLine(name, githubToken, gitRepo),
                getGitRepoMemberResponses(gitRepo, githubToken));
    }
    public GitRepoResponse findGitRepoInfosAndUpdate(final String name) {
        if (gitRepoServiceImpl.gitRepoExistsByName(name)) {
            String githubToken = authService.getLoginUser().getGithubToken();
            GitRepo gitRepo = gitRepoServiceImpl.findGitRepo(name);
            return new GitRepoResponse(
                    gitRepoServiceImpl.updateAndGetSparkLine(name, githubToken, gitRepo),
                    getGitRepoMemberResponses(gitRepo, githubToken));
        }

        int year = LocalDate.now().getYear();
        String githubToken = authService.getLoginUser().getGithubToken();

        return new GitRepoResponse(
                gitRepoServiceImpl.updateAndGetSparkLine(name, githubToken, gitRepoServiceImpl.findGitRepo(name)),
                getGitRepoMemberResponses(name, year, githubToken));
    }

    public TwoGitRepoMemberResponse findMembersByGitRepoForCompare(final GitRepoCompareRequest request) {
        String githubToken = authService.getLoginUser().getGithubToken();
        return new TwoGitRepoMemberResponse(
                getGitRepoMemberResponses(gitRepoServiceImpl.findGitRepo(request.getFirstRepo()), githubToken),
                getGitRepoMemberResponses(gitRepoServiceImpl.findGitRepo(request.getSecondRepo()), githubToken));
    }

    @Override
    public void saveAllGitRepoMembers(final Set<GitRepoMember> gitRepoMembers) {
        gitRepoMemberServiceImpl.saveAllGitRepoMembers(gitRepoMembers);
    }

    @Override
    public void saveAll(final Set<GitRepo> gitRepos) {
        gitRepoServiceImpl.saveAll(gitRepos);
    }

    @Override
    public boolean gitRepoExistsByName(final String name) {
        return gitRepoServiceImpl.gitRepoExistsByName(name);
    }
}
