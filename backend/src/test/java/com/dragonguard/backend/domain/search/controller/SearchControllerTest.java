package com.dragonguard.backend.domain.search.controller;

import com.dragonguard.backend.domain.result.dto.response.GitRepoResultResponse;
import com.dragonguard.backend.domain.result.dto.response.UserResultResponse;
import com.dragonguard.backend.domain.search.service.SearchService;
import com.dragonguard.backend.support.docs.RestDocumentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.dragonguard.backend.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.dragonguard.backend.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("search 컨트롤러의")
@WebMvcTest(SearchController.class)
class SearchControllerTest extends RestDocumentTest {
    @MockBean
    private SearchService searchService;

    @Test
    @DisplayName("검색 결과 조회")
    void getSearchResult() throws Exception {
        // given
        List<UserResultResponse> expected = Arrays.asList(
                new UserResultResponse(1L, "ohksj77"),
                new UserResultResponse(2L, "HJ39"),
                new UserResultResponse(3L, "posite"),
                new UserResultResponse(4L, "Sammuelwoojae"),
                new UserResultResponse(5L, "And"),
                new UserResultResponse(6L, "DragonGuard-JinJin"));

        given(searchService.getUserSearchResultByClient(any(), any(), any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/search?page=1&name=gitrank&type=USERS")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // docs
        perform.andDo(print())
                .andDo(document("get search result", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("검색 결과 필터링 조회")
    void getSearchResultByFiltering() throws Exception {
        // given
        List<GitRepoResultResponse> expected = List.of(
                new GitRepoResultResponse(1L, "tukcom2023CD/DragonGuard-JinJin", "java", "블록체인기반깃랭크시스템", LocalDateTime.now().toString()),
                new GitRepoResultResponse(2L, "tukcom2023CD/DragonGuard", "swift", "블록체인기반깃랭크시스템", LocalDateTime.now().toString()),
                new GitRepoResultResponse(3L, "tukcom2023CD/JinJin", "java", "진진시스템", LocalDateTime.now().toString()),
                new GitRepoResultResponse(4L, "tukcom2023CD/Dragon", "java", "드래곤시스템", LocalDateTime.now().toString()),
                new GitRepoResultResponse(5L, "tukcom2023CD/Dragon-Jin", "java", "드래곤진", LocalDateTime.now().toString())
        );

        given(searchService.getGitRepoSearchResultByClient(any(), any(), any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/search?page=1&name=gitrank&type=REPOSITORIES&filters=language:swift,language:kotlin,language:java")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // docs
        perform.andDo(print())
                .andDo(document("get search result by filtering", getDocumentRequest(), getDocumentResponse()));
    }
}
