package com.dragonguard.android.connect

import android.util.Log
import com.dragonguard.android.BuildConfig
import com.dragonguard.android.model.*
import com.dragonguard.android.model.compare.CompareRepoMembersResponseModel
import com.dragonguard.android.model.compare.CompareRepoRequestModel
import com.dragonguard.android.model.compare.CompareRepoResponseModel
import com.dragonguard.android.model.contributors.RepoContributorsModel
import com.dragonguard.android.model.detail.UserDetailModel
import com.dragonguard.android.model.klip.*
import com.dragonguard.android.model.org.*
import com.dragonguard.android.model.rankings.OrgInternalRankingModel
import com.dragonguard.android.model.rankings.OrganizationRankingModel
import com.dragonguard.android.model.rankings.TotalUsersRankingModelItem
import com.dragonguard.android.model.search.RepoSearchResultModel
import com.dragonguard.android.model.token.RefreshTokenModel
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/*
 서버에 요청하는 모든 api들의 호출부분
 */
class ApiRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()

    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.api)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var api = retrofit.create(GitRankAPI::class.java)

    //Repository 검색을 위한 함수
    fun getRepositoryNames(name: String, count: Int, type: String, token: String): ArrayList<RepoSearchResultModel> {
        var repoNames : ArrayList<RepoSearchResultModel> = arrayListOf<RepoSearchResultModel>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page","${count+1}")
        queryMap.put("name",name)
        queryMap.put("type",type)

        Log.d("api 호출", "$count 페이지 검색")

        val repoName = api.getRepoName(queryMap, "Bearer $token")
        try{
            val result = repoName.execute()
            repoNames = result.body()!!
        }catch (e : Exception){
            Log.d("error", "레포 필터없는 검색: ${e.message}")
            return repoNames
        }
        return repoNames
    }

    fun getRepositoryNamesWithFilters(name: String, count: Int, filters: String, type: String, token: String): ArrayList<RepoSearchResultModel> {
        var repoNames : ArrayList<RepoSearchResultModel> = arrayListOf<RepoSearchResultModel>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page","${count+1}")
        queryMap.put("name",name)
        queryMap.put("type",type)
        queryMap.put("filters", filters)
        Log.d("api 호출", "이름: $name, type: $type filters: $filters")
        Log.d("api 호출", "$count 페이지 검색")

        val repoName = api.getRepoName(queryMap, "Bearer $token")
        try{
            val result = repoName.execute()
            repoNames = result.body()!!
        }catch (e : Exception){
            Log.d("error", "레포 필터별 검색: ${e.message}")
            return repoNames
        }
        return repoNames
    }

    //사용자의 정보를 받아오기 위한 함수
    fun getUserInfo(token: String): UserInfoModel {
        val userInfo = api.getUserInfo("Bearer $token")
        var userResult = UserInfoModel(null, null, null, null, null, null, null, null,null, null, null,
            null, null, null, null, null)
        try {
            val result = userInfo.execute()
            Log.d("no", "사용자 정보 요청 결과 : ${result.code()}")
            userResult = result.body()!!
        } catch (e: Exception) {
            Log.d("exception", "exception : ${e.printStackTrace()}")
            return userResult
        }
        return userResult
    }

    //Repository의 기여자들의 정보를 받아오기 위한 함수
    fun getRepoContributors(repoName: String, token: String): RepoContributorsModel {
        val repoContributors = api.getRepoContributors(repoName, "Bearer $token")
        var repoContResult = RepoContributorsModel(null, null)
        try{
            val result = repoContributors.execute()
            repoContResult = result.body()!!
        } catch (e: Exception) {
            return repoContResult
        }
        return repoContResult
    }

    //klip wallet을 등록한 모든 사용자의 토큰에 따른 등수를 받아오는 함수
    fun getTotalUsersRankings(page: Int, size: Int, token: String): ArrayList<TotalUsersRankingModelItem> {
        var rankingResult = ArrayList<TotalUsersRankingModelItem>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page","$page")
        queryMap.put("size","$size")
        queryMap.put("sort","tokens,DESC")
        val ranking = api.getTotalUsersRanking(queryMap, "Bearer $token")
        try {
            val result = ranking.execute()
            rankingResult = result.body()!!
        } catch (e: Exception) {
            Log.d("error", "유저랭킹 api 에러 ${e.message}")
            return rankingResult
        }
        return rankingResult
    }

    //사용자의 토큰 부여 내역을 확인하기 위한 함수
    fun getTokenHistory(token: String): ArrayList<TokenHistoryModelItem>? {
        val tokenHistory = api.getTokenHistory("Bearer $token")
        var tokenHistoryResult: ArrayList<TokenHistoryModelItem>? = null
        try {
            val result = tokenHistory.execute()
            tokenHistoryResult = result.body()!!
        } catch (e: Exception) {
            return null
        }
        return tokenHistoryResult
    }

    //klip wallet address를 서버에 등록하기 위한 함수
    fun postWalletAddress(body: WalletAddressModel, token: String): Boolean {
        val walletAddress = api.postWalletAddress(body, "Bearer $token")
        return try{
            val result = walletAddress.execute()
            Log.d("dd", "지갑주소 전송 결과 : ${result.code()} ${body.walletAddress}")
            result.isSuccessful
        } catch (e: Exception) {
            Log.d("dd", "결과 실패")
            false
        }
    }

    //kilp wallet address의 정보제공을 위한 함수
    fun postWalletAuth(body: WalletAuthRequestModel): WalletAuthResponseModel {
        var authResult = WalletAuthResponseModel(null, null,null)
        val retrofitWallet = Retrofit.Builder().baseUrl(BuildConfig.prepare)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiWallet = retrofitWallet.create(GitRankAPI::class.java)
        val authWallet = apiWallet.postWalletAuth(body)
        try{
            val result = authWallet.execute()
            authResult = result.body()!!
        } catch (e: Exception) {
            return authResult
        }
        return authResult
    }

    //klip wallet address 정보제공동의 결과를 받아오는 함수
    fun getAuthResult(key: String): WalletAuthResultModel {
        var authResult = WalletAuthResultModel(null, null,null,null)
        val retrofitWallet = Retrofit.Builder().baseUrl(BuildConfig.prepare)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiWallet = retrofitWallet.create(GitRankAPI::class.java)
        val authWallet = apiWallet.getAuthResult(key)
        try{
            val result = authWallet.execute()
            authResult = result.body()!!
        } catch (e: Exception) {
            return authResult
        }
        return authResult
    }

    //두 Repository의 구성원들의 기여도를 받아오기 위한 함수
    fun postCompareRepoMembersRequest(body: CompareRepoRequestModel, token: String): CompareRepoMembersResponseModel {
        Log.d("token", "token: $token")
        var compareRepoResult = CompareRepoMembersResponseModel(null, null)
        val compareRepoMembers = api.postCompareRepoMembers(body, "Bearer $token")
        try{
            val result = compareRepoMembers.execute()
            compareRepoResult = result.body()!!
            Log.d("token", "1 결과 ${result.code()}")
        } catch (e: Exception) {
            Log.d("token", "1 결과 ${e.printStackTrace()}")
            return compareRepoResult
        }
        return compareRepoResult
    }

    //두 Repository의 정보를 받아오기 위한 함수
    fun postCompareRepoRequest(body: CompareRepoRequestModel, token: String): CompareRepoResponseModel {
        Log.d("token", "token: $token")
        var compareRepoResult = CompareRepoResponseModel(null, null)
        val compareRepo = api.postCompareRepo(body, "Bearer $token")
        try{
            val result = compareRepo.execute()
            compareRepoResult = result.body()!!
            Log.d("token", "2 결과 ${result.code()}")
        } catch (e: Exception) {
            Log.d("token", "2 결과 ${e.printStackTrace()}")
            return compareRepoResult
        }
        return compareRepoResult
    }

    fun getNewAccessToken(access: String, refresh: String): RefreshTokenModel {
        var newToken = RefreshTokenModel(null,null,null)
        val getToken = api.getNewAccessToken(access, refresh)
        try {
            val result = getToken.execute()
            newToken = result.body()!!
            Log.d("e", "result ${result.code()}  ${result.message()}")
        } catch (e: Exception) {
            Log.d("e", "error ${e.printStackTrace()}")
            return newToken
        }
        return newToken
    }

    fun postCommits(token: String) {
        val postCommit = api.postCommits("Bearer $token")
        try{
            val result = postCommit.execute()
            Log.d("postCommits", "result ${result.code()}")
        } catch (e: Exception) {
            Log.d("e", "error ${e.printStackTrace()}")
        }
    }

    fun getOrgNames(name: String, token: String, count: Int, type: String): OrganizationNamesModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page","$count")
        queryMap.put("name",name)
        queryMap.put("type",type)
        queryMap.put("size", "10")
        val getOrgNames = api.getOrgNames(queryMap, "Bearer $token")
        var orgNames = OrganizationNamesModel()
        orgNames.add(OrganizationNamesModelItem(null, null, null, null, null))
        return try{
            val result = getOrgNames.execute()
            orgNames = result.body()!!
            orgNames
        } catch (e: Exception) {
            Log.d("error", "error : ${e.printStackTrace()}")
            orgNames
        }
    }

    fun postRegistOrg(body: RegistOrgModel, token: String): RegistOrgResultModel {
        val postRegist = api.postOrgRegist(body, "Bearer $token")
        var registResult = RegistOrgResultModel(0)
        return try {
            val result = postRegist.execute()
            registResult = result.body()!!
            registResult
        } catch (e: Exception) {
            Log.d("error", "RegisterOrganization error: ${e.message}")
            registResult
        }
    }

    fun addOrgMember(body: AddOrgMemberModel, token: String): Long {
        val addOrg = api.postAddOrgMember(body, "Bearer $token")
        return try {
            val result = addOrg.execute()
            Log.d("status", "조직 멤버 추가 결과 : ${result.code()}")
            result.body()!!.id
        } catch (e: Exception) {
            Log.d("status", "조직 멤버 추가 error : ${e.message}")
            -1
        }
    }

    fun sendEmailAuth(token: String): Long {
        val sendEmail = api.postAuthEmail("Bearer $token")
        return try{
            val result = sendEmail.execute()
            result.body()!!.id
        } catch (e: Exception) {
            Log.d("status", "이메일 인증 시도 error : ${e.message}")
            -1L
        }
    }

    fun emailAuthResult(id: Long, code: String, token: String): Boolean {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("id", id.toString())
        queryMap.put("code", code)
        val emailAuth = api.getEmailAuthResult(queryMap, "Bearer $token")
        return try {
            val result = emailAuth.execute()
            result.body()!!.is_valid_code
        } catch (e: Exception) {
            Log.d("status", "이메일 인증 결과 error : ${e.message}")
            false
        }
    }

    fun deleteEmailCode(id: Long, token: String): Boolean {
        val delete = api.deleteEmailCode(id, "Bearer $token")
        return try {
            val result = delete.execute()
            Log.d("status", "이메일 인증 코드 삭제 성공: ${result.code()}")
            true
        } catch (e: Exception) {
            Log.d("status", "이메일 인증 코드 삭제 실패: ${e.message}")
            false
        }
    }

    fun searchOrgId(orgName: String, token: String): Long {
        val searchId = api.getOrgId(orgName, "Bearer $token")
        val resultId = 0L
        return try{
            val result = searchId.execute()
            return result.body()!!.id
        }catch (e: Exception) {
            Log.d("status", "조직 id 검색 실패: ${e.message}")
            resultId
        }
    }

    fun orgInternalRankings(id: Long, count: Int, token: String): OrgInternalRankingModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("organizationId", id.toString())
        queryMap.put("page", count.toString())
        queryMap.put("size", "20")

        val orgRankings = OrgInternalRankingModel()
        val orgInternal = api.getOrgInternalRankings(queryMap, "Bearer $token")

        return try {
            val result = orgInternal.execute()
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "조직 내 사용자들의 랭킹 조회 실패: ${e.message} ")
            return orgRankings
        }
    }

    fun typeOrgRanking(type: String, page: Int, token:String): OrganizationRankingModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("type", type)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")

        val orgRankings = OrganizationRankingModel()
        val orgTotal = api.getOrgRankings(queryMap, "Bearer $token")
        return try{
            val result = orgTotal.execute()
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "전체 조직 랭킹 조회 실패: ${e.message} ")
            return orgRankings
        }
    }

    fun allOrgRanking(page: Int, token: String): OrganizationRankingModel {
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")
        val orgRankings = OrganizationRankingModel()
        val orgTotal = api.getAllOrgRankings(queryMap,"Bearer $token")
        return try{
            val result = orgTotal.execute()
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "전체 조직 랭킹 조회 실패: ${e.message} ")
            return orgRankings
        }
    }

    fun userDetail(githubId: String, token: String): UserDetailModel {
        val details = UserDetailModel(null, null, null)
        val userDetails = api.getUserDetail(githubId, "Bearer $token")
        return try {
            val result = userDetails.execute()
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "사용자 $githubId 의 상세조회 실패: ${e.message} ")
            return details
        }
    }

    fun checkAdmin(token: String): Boolean {
        val check = api.getPermissionState("Bearer $token")
        try {
            val result = check.execute()
            if(result.isSuccessful){
                return true
            } else if(result.code() == 403) {
                Log.d("error", "admin error result ${result.code()}")
                return false
            }
        } catch (e: HttpException) {
            if (e.code() == 403) {
                Log.d("error", "admin error ${e.code()}")
                return false
            }
        }catch (e: Exception) {
            Log.d("error", "admin error")
        }
        return false
    }

    fun approveOrgRequest(body: OrgApprovalModel, token: String): ApproveRequestOrgModel {
        val approve = api.postOrgApproval(body, "Bearer $token")
        try{
            val result = approve.execute()
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "admin error ${e.message}")
        }
        return ApproveRequestOrgModel()
    }

    fun statusOrgList(status: String, page: Int, token: String): ApproveRequestOrgModel{
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("status", status)
        queryMap.put("page", page.toString())
        queryMap.put("size", "20")

        val statusOrg = ApproveRequestOrgModel()
        val orgList = api.getOrgStatus(queryMap,"Bearer $token")
        return try{
            val result = orgList.execute()
            return result.body()!!
        } catch (e: Exception) {
            Log.d("error", "전체 조직 랭킹 조회 실패: ${e.message} ")
            return statusOrg
        }
    }

    fun userGitOrgRepoList(orgName: String, token: String): GithubOrgReposModel? {
        val repoList = api.getOrgRepoList(orgName, token)
        return try {
            val result = repoList.execute()
            result.body()
        } catch (e: Exception) {
            Log.d("error", "조직의 레포 리스트 조회 실패: ${e.message}")
            null
        }
    }
}