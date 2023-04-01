package com.dragonguard.backend.gitrepomember.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author 김승진
 * @description 깃허브 Repository 관련 멤버 기여도 정보를 Github REST API에서 응답을 받아 담는 dto
 */

@Getter
@Builder
@ToString
@AllArgsConstructor
public class GitRepoMemberResponse {
    private String githubId;
    private Integer commits;
    private Integer additions;
    private Integer deletions;
}
