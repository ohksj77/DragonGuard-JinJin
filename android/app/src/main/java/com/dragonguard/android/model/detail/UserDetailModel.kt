package com.dragonguard.android.model.detail

data class UserDetailModel(
    val authStep: String?,
    val comments: Int?,
    val commits: Int?,
    val gitOrganizations: List<String>?,
    val githubId: String?,
    val id: String?,
    val issues: Int?,
    val name: String?,
    val organization: String?,
    val organizationRank: Int?,
    val profileImage: String?,
    val pullRequests: Int?,
    val rank: Int?,
    val tier: String?,
    val tokenAmount: Int?
)