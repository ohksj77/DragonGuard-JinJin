package com.dragonguard.backend.member.client;

import com.dragonguard.backend.config.github.GithubProperties;
import com.dragonguard.backend.global.exception.WebClientException;
import com.dragonguard.backend.member.dto.request.client.MemberClientRequest;
import com.dragonguard.backend.member.dto.response.client.MemberOrganizationResponse;
import com.dragonguard.backend.util.GithubClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;

/**
 * @author 김승진
 * @description WebClient로 멤버의 조직을 가져오는 클라이언트
 */

@Component
public class MemberOrganizationClient implements GithubClient<MemberClientRequest, MemberOrganizationResponse[]> {
    private final GithubProperties githubProperties;
    private final WebClient webClient;
    private static final String GITHUB_API_MIME_TYPE = "application/vnd.github+json";
    private static final String USER_AGENT = "SPRING BOOT WEB CLIENT";

    public MemberOrganizationClient(GithubProperties githubProperties) {
        this.githubProperties = githubProperties;
        webClient = generateWebClient();
    }

    @Override
    public MemberOrganizationResponse[] requestToGithub(MemberClientRequest request) {
        return webClient.get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path("users/")
                                .path(request.getGithubId())
                                .path("/orgs")
                                .build())
                .headers(headers -> headers.setBearerAuth(request.getGithubToken()))
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(MemberOrganizationResponse[].class)
                .blockOptional()
                .orElseThrow(WebClientException::new);
    }

    private WebClient generateWebClient() {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                .build();
        return WebClient.builder()
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(githubProperties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, GITHUB_API_MIME_TYPE)
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .defaultHeader(githubProperties.getVersionKey(), githubProperties.getVersionValue())
                .build();
    }
}
