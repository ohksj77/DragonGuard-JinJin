package com.dragonguard.android.connect

import android.util.Log
import com.dragonguard.android.BuildConfig
import com.dragonguard.android.model.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/*
 서버에 요청하는 모든 api들의 호출부분
 */
class ApiRepository {
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()

    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.api)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var api = retrofit.create(GitRankAPI::class.java)

    //Repository 검색을 위한 함수
    fun getRepositoryNames(name: String, count: Int, type: String): ArrayList<RepoSearchResultModel> {
        var repoNames : ArrayList<RepoSearchResultModel> = arrayListOf<RepoSearchResultModel>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page","${count+1}")
        queryMap.put("name",name)
        queryMap.put("type",type)

        Log.d("api 호출", "$count 페이지 검색")

        val repoName = api.getRepoName(queryMap)
        try{
            val result = repoName.execute()
            if(result.isSuccessful){
                repoNames = result.body()!!
            }
        }catch (e : SocketTimeoutException){
            return repoNames
        }
        return repoNames
    }

    fun getRepositoryNamesWithFilters(name: String, count: Int, filters: String, type: String): ArrayList<RepoSearchResultModel> {
        var repoNames : ArrayList<RepoSearchResultModel> = arrayListOf<RepoSearchResultModel>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page","${count+1}")
        queryMap.put("name",name)
        queryMap.put("type",type)
        queryMap.put("filters", filters)

        Log.d("api 호출", "$count 페이지 검색")

        val repoName = api.getRepoName(queryMap)
        try{
            val result = repoName.execute()
            if(result.isSuccessful){
                repoNames = result.body()!!
            }
        }catch (e : SocketTimeoutException){
            return repoNames
        }
        return repoNames
    }

    //사용자의 정보를 받아오기 위한 함수
    fun getUserInfo(token: String): UserInfoModel {
        val userInfo = api.getUserInfo("Bearer $token")
        var userResult = UserInfoModel(null, null, null, null, null, null, null, null,null)
        try {
            val result = userInfo.execute()
            Log.d("no", "사용자 정보 요청 결과 : ${result.code()}")
            userResult = result.body()!!
            Log.d("결과", "사용자 정보 : $userResult")
        } catch (e: Exception) {
            return userResult
        }
        return userResult
    }

    //Repository의 기여자들의 정보를 받아오기 위한 함수
    fun getRepoContributors(repoName: String): ArrayList<RepoContributorsItem> {
        val repoContributors = api.getRepoContributors(repoName)
        var repoContResult = arrayListOf(RepoContributorsItem(null,null,null,null))
        try{
            val result = repoContributors.execute()
            if(result.isSuccessful) {
                repoContResult = result.body()!!
            }
        } catch (e: Exception) {
            return repoContResult
        }
        return repoContResult
    }

    //klip wallet을 등록한 모든 사용자의 토큰에 따른 등수를 받아오는 함수
    fun getTotalUsersRankings(page: Int, size: Int): ArrayList<TotalUsersRankingModelItem> {
        var rankingResult = ArrayList<TotalUsersRankingModelItem>()
        val queryMap = mutableMapOf<String, String>()
        queryMap.put("page","${page}")
        queryMap.put("size","$size")
        queryMap.put("sort","tokens,DESC")
        val ranking = api.getTotalUsersRanking(queryMap)
        try {
            val result = ranking.execute()
            if(result.isSuccessful) {
                rankingResult = result.body()!!
            }
        } catch (e: Exception) {
            Log.d("error", "유저랭킹 api 에러 ${e.message}")
            return rankingResult
        }
        return rankingResult
    }

    //사용자의 githubid를 등록하는 함수
    fun postRegister(body: RegisterGithubIdModel): Int {
        val register = api.postGithubId(body)
        var registerResult = RegisterGithubIdResponseModel(0)
        try{
            val result = register.execute()
            if(result.isSuccessful) {
                registerResult = result.body()!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("error", "유저 등록 에러 ${e.message}")
            return 0
        }
        return registerResult.id
    }

    //사용자의 토큰 부여 내역을 확인하기 위한 함수
    fun getTokenHistory(id: Int): ArrayList<TokenHistoryModelItem> {
        val tokenHistory = api.getTokenHistory(id)
        var tokenHistoryResult = arrayListOf(TokenHistoryModelItem(null,null,null,null, null))
        try {
            val result = tokenHistory.execute()
            if(result.isSuccessful) {
                tokenHistoryResult = result.body()!!
            }
        } catch (e: Exception) {
            return tokenHistoryResult
        }
        return tokenHistoryResult
    }

    //klip wallet address를 서버에 등록하기 위한 함수
    fun postWalletAddress(body: WalletAddressModel): Boolean {
        val walletAddress = api.postWalletAddress(body)
        try{
            val result = walletAddress.execute()
            Log.d("dd", "지갑주소 전송 결과 : ${result.code()} ${body.walletAddress}")
            return result.isSuccessful
        } catch (e: Exception) {
            Log.d("dd", "결과 실패")
            return false
        }
    }

    //kilp wallet address의 정보제공을 위한 함수
    fun postWalletAuth(body: WalletAuthRequestModel): WalletAuthResponseModel  {
        var authResult = WalletAuthResponseModel(null, null,null)
        val retrofitWallet = Retrofit.Builder().baseUrl(BuildConfig.prepare)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiWallet = retrofitWallet.create(GitRankAPI::class.java)
        val authWallet = apiWallet.postWalletAuth(body)
        try{
            val result = authWallet.execute()
            if(result.isSuccessful) {
                authResult = result.body()!!
            }
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
            if(result.isSuccessful) {
                authResult = result.body()!!
            }
        } catch (e: Exception) {
            return authResult
        }
        return authResult
    }

    //두 Repository의 구성원들의 기여도를 받아오기 위한 함수
    fun postCompareRepoMembersRequest(body: CompareRepoRequestModel): CompareRepoMembersResponseModel {
        var compareRepoResult = CompareRepoMembersResponseModel(null, null)
        val compareRepoMembers = api.postCompareRepoMembers(body)
        try{
            val result = compareRepoMembers.execute()
            if(result.isSuccessful) {
                compareRepoResult = result.body()!!
            }
        } catch (e: Exception) {
            return compareRepoResult
        }
        return compareRepoResult
    }

    //두 Repository의 정보를 받아오기 위한 함수
    fun postCompareRepoRequest(body: CompareRepoRequestModel): CompareRepoResponseModel {
        var compareRepoResult = CompareRepoResponseModel(null, null)
        val compareRepo = api.postCompareRepo(body)
        try{
            val result = compareRepo.execute()
            if(result.isSuccessful) {
                compareRepoResult = result.body()!!
            }
        } catch (e: Exception) {
            return compareRepoResult
        }
        return compareRepoResult
    }

    fun getAccessToken(code: String):AccessTokenModel {
        val tokenResult = AccessTokenModel(null,null,null)

        val retrofitAccess = Retrofit.Builder().baseUrl(BuildConfig.oauth)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val queryMap = mutableMapOf<String, String>()
        queryMap.put("client_id", BuildConfig.clientId)
        queryMap.put("client_secret", BuildConfig.clientSecret)
        queryMap.put("code", code)

        val apis = retrofitAccess.create(GitRankAPI::class.java)
        val token = apis.getAccessToken(queryMap)

        return try {
            val result = token.execute()
            result.body()!!
        }catch (e:Exception) {
            tokenResult
        }
    }

    fun getOauthUserInfo(token: String): OauthUserInfoModel? {
        val oauthUser: OauthUserInfoModel
        val retro = Retrofit.Builder().baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apis = retro.create(GitRankAPI::class.java)
        val oauthInfo = apis.getOauthUserInfo("token $token")
        return try {
            val result = oauthInfo.execute()
            oauthUser = result.body()!!
            oauthUser
        } catch (e: Exception) {
            null
        }
    }
}