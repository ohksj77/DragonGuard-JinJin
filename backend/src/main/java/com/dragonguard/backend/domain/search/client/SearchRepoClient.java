package com.dragonguard.backend.domain.search.client;

import com.dragonguard.backend.domain.search.dto.client.SearchRepoResponse;
import com.dragonguard.backend.domain.search.dto.request.SearchRequest;
import com.dragonguard.backend.global.GithubClient;
import com.dragonguard.backend.global.exception.WebClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

/**
 * @author 김승진
 * @description Repository 검색에 대한 Github REST API 요청을 수행하는 클래스
 */

@Component
@RequiredArgsConstructor
public class SearchRepoClient implements GithubClient<SearchRequest, SearchRepoResponse> {
    private final WebClient webClient;

    @Override
    public SearchRepoResponse requestToGithub(SearchRequest request) {
        return webClient.get()
                .uri(getUriBuilder(request))
                .headers(headers -> headers.setBearerAuth(request.getGithubToken()))
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .retrieve()
                .bodyToMono(SearchRepoResponse.class)
                .blockOptional()
                .orElseThrow(WebClientException::new);
    }

    private Function<UriBuilder, URI> getUriBuilder(SearchRequest request) {
        List<String> filters = request.getFilters();

        if (filters == null || filters.isEmpty()) {
            return uriBuilder -> uriBuilder
                    .path("search")
                    .path("/" + request.getType().toString().toLowerCase())
                    .queryParam("q", request.getName().strip())
                    .queryParam("per_page", 10)
                    .queryParam("page", request.getPage())
                    .build();
        }
        String query = String.join(" ", filters);

        return uriBuilder -> uriBuilder
                .path("search")
                .path("/" + request.getType().toString().toLowerCase())
                .queryParam("q", request.getName().strip().concat(query))
                .queryParam("per_page", 10)
                .queryParam("page", request.getPage())
                .build();
    }
}
