package com.dragonguard.backend.search.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 김승진
 * @description 검색 유형을 구분하는 enum
 */

@Getter
@AllArgsConstructor
public enum SearchType {
    USERS, REPOSITORIES
}
