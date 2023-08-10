package com.dragonguard.backend.domain.organization.controller;

import com.dragonguard.backend.domain.organization.dto.request.AddMemberRequest;
import com.dragonguard.backend.domain.organization.dto.request.OrganizationRequest;
import com.dragonguard.backend.domain.organization.dto.response.OrganizationResponse;
import com.dragonguard.backend.domain.organization.entity.OrganizationType;
import com.dragonguard.backend.domain.organization.service.OrganizationEmailFacade;
import com.dragonguard.backend.global.dto.IdResponse;
import com.dragonguard.backend.support.docs.RestDocumentTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.dragonguard.backend.support.docs.ApiDocumentUtils.getDocumentRequest;
import static com.dragonguard.backend.support.docs.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("organization 컨트롤러의")
@WebMvcTest(OrganizationController.class)
class OrganizationControllerTest extends RestDocumentTest {
    @MockBean
    private OrganizationEmailFacade organizationService;

    @Test
    @DisplayName("조직 생성이 수행되는가")
    void postOrganization() throws Exception {
        // given
        IdResponse<Long> expected = new IdResponse<>(1L);
        given(organizationService.saveOrganization(any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/organizations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        toRequestBody(new OrganizationRequest("한국공학대학교", OrganizationType.UNIVERSITY, "tukorea.ac.kr")))
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk());

        // docs
        perform.andDo(print())
                .andDo(document("create organization", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("조직 id 조회가 수행되는가")
    void getOrganization() throws Exception {
        // given
        IdResponse<Long> expected = new IdResponse<>(1L);
        given(organizationService.getByName(any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/organizations/search-id?name=한국공학대학교")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk());

        // docs
        perform.andDo(print())
                .andDo(document("get organization id", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("조직에 멤버 추가가 수행되는가")
    void addMemberToOrganization() throws Exception {
        // given
        IdResponse<Long> expected = new IdResponse<>(1L);
        given(organizationService.addMemberAndSendEmail(any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        post("/organizations/add-member")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        toRequestBody(
                                                new AddMemberRequest(1L, "ohksj77@tukorea.ac.kr")))
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk());

        // docs
        perform.andDo(print())
                .andDo(document("add member to organization", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("타입별 조직 목록 조회가 수행되는가")
    void getOrganizations() throws Exception {
        // given
        List<OrganizationResponse> expected = List.of(
                new OrganizationResponse(1L, "한국공학대학교", OrganizationType.UNIVERSITY, "tukorea.ac.kr", 100000L),
                new OrganizationResponse(2L, "서울대학교", OrganizationType.UNIVERSITY, "snu.ac.kr", 10000L),
                new OrganizationResponse(3L, "KAIST", OrganizationType.UNIVERSITY, "kaist.ac.kr", 1000L));
        given(organizationService.findByType(any(), any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/organizations?type=UNIVERSITY&page=1&size=20")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk());

        // docs
        perform.andDo(print())
                .andDo(document("get organization by type", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("전체 조직 랭킹 조회가 수행되는가")
    void getOrganizationRank() throws Exception {
        // given
        List<OrganizationResponse> expected = List.of(
                new OrganizationResponse(1L, "한국공학대학교", OrganizationType.UNIVERSITY, "tukorea.ac.kr", 100000L),
                new OrganizationResponse(2L, "서울대학교", OrganizationType.UNIVERSITY, "snu.ac.kr", 10000L),
                new OrganizationResponse(3L, "KAIST", OrganizationType.UNIVERSITY, "kaist.ac.kr", 1000L));
        given(organizationService.getOrganizationRank(any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/organizations/ranking/all?page=0&size=20")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk());

        // docs
        perform.andDo(print())
                .andDo(document("get organization ranking", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("조직 타입별 랭킹 조회가 수행되는가")
    void getOrganizationRankByType() throws Exception {
        // given
        List<OrganizationResponse> expected = List.of(
                new OrganizationResponse(1L, "한국공학대학교", OrganizationType.UNIVERSITY, "tukorea.ac.kr", 100000L),
                new OrganizationResponse(2L, "서울대학교", OrganizationType.UNIVERSITY, "snu.ac.kr", 10000L),
                new OrganizationResponse(3L, "KAIST", OrganizationType.UNIVERSITY, "kaist.ac.kr", 1000L));
        given(organizationService.getOrganizationRankByType(any(), any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/organizations/ranking?type=UNIVERSITY&page=1&size=20")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk());

        // docs
        perform.andDo(print())
                .andDo(document("get organization ranking by type", getDocumentRequest(), getDocumentResponse()));
    }

    @Test
    @DisplayName("조직 검색이 수행되는가")
    void searchOrganization() throws Exception {
        // given
        List<OrganizationResponse> expected = List.of(
                new OrganizationResponse(1L, "한국공학대학교", OrganizationType.UNIVERSITY, "tukorea.ac.kr", 100000L),
                new OrganizationResponse(2L, "서울대학교", OrganizationType.UNIVERSITY, "snu.ac.kr", 10000L),
                new OrganizationResponse(3L, "KAIST", OrganizationType.UNIVERSITY, "kaist.ac.kr", 1000L));
        given(organizationService.searchOrganization(any(), any(), any())).willReturn(expected);

        // when
        ResultActions perform =
                mockMvc.perform(
                        get("/organizations/search?type=UNIVERSITY&name=한국공학대학교&page=1&size=20")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer apfawfawfa.awfsfawef2.r4svfv32"));

        // then
        perform.andExpect(status().isOk());

        // docs
        perform.andDo(print())
                .andDo(document("search organization", getDocumentRequest(), getDocumentResponse()));
    }
}
