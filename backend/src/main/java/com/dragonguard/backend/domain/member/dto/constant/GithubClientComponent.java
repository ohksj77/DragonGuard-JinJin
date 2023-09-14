package com.dragonguard.backend.domain.member.dto.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GithubClientComponent {
    COMMIT_COMPONENT("memberCommitClient"),
    ISSUE_COMPONENT("memberIssueClient"),
    PULL_REQUEST_COMPONENT("memberPullRequestClient"),
    CODE_REVIEW_COMPONENT("memberCodeReviewClient");

    private final String toComponentName;

    public String getName() {
        return this.toComponentName;
    }
}
