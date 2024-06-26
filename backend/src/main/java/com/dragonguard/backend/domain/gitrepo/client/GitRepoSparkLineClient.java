package com.dragonguard.backend.domain.gitrepo.client;

import com.dragonguard.backend.domain.gitrepo.dto.client.GitRepoClientRequest;
import com.dragonguard.backend.domain.gitrepo.dto.client.GitRepoSparkLineResponse;
import com.dragonguard.backend.global.exception.WebClientException;
import com.dragonguard.backend.global.template.client.GithubClient;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author 김승진
 * @description 레포지토리의 스파크 라인 정보를 요청하는 클라이언트 클래스
 */
@Component
@RequiredArgsConstructor
public class GitRepoSparkLineClient
        implements GithubClient<GitRepoClientRequest, GitRepoSparkLineResponse> {
    private static final String PATH_FORMAT = "repos/%s/stats/participation";
    private final WebClient webClient;

    @Override
    public GitRepoSparkLineResponse requestToGithub(final GitRepoClientRequest request) {
        return webClient
                .get()
                .uri(
                        uriBuilder ->
                                uriBuilder
                                        .path(String.format(PATH_FORMAT, request.getName()))
                                        .build())
                .headers(headers -> headers.setBearerAuth(request.getGithubToken()))
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(GitRepoSparkLineResponse.class)
                .blockOptional()
                .orElseThrow(WebClientException::new);
    }
}
