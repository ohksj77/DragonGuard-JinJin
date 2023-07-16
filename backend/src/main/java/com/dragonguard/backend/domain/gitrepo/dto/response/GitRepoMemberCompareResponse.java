package com.dragonguard.backend.domain.gitrepo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author 김승진
 * @description 깃허브 Repository 멤버 비교 정보를 담는 응답 dto
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GitRepoMemberCompareResponse {
    private GitRepoMemberResponse firstMember;
    private GitRepoMemberResponse secondMember;
}
