package com.example.gravit.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//로그인
data class IdTokenRequest(val idToken: String)
data class AuthTokenResponse(val accessToken: String, val isOnboarded : Boolean)

//온보딩
data class OnboardingRequest(val nickname: String, val profilePhotoNumber: Int)
data class OnboardingResponse(val userId: Int, val profileImgNumber: Int, val nickname: String, val providerId: String)

//챕터
data class ChapterPageResponse(
    val chapterId: Int,
    val name: String,
    val description: String,
    val totalUnits: Int,
    val completedUnits: Int
)

//홈
data class MainPageResponse(
    val nickname: String,
    val leagueName: String,
    val xp: Int,
    val level: Int,
    val planetConquestRate: Int,
    val consecutiveDays: Int,
    val chapterId: Int,
    val chapterName: String,
    val chapterDescription: String,
    val totalUnits: Int,
    val completedUnits: Int,
    val missionName: String,
    val awardXp: Int,
    val isCompleted: Boolean
)

// 유닛
data class UnitPageResponse(
    val unitProgressDetailResponse: UnitProgressDetailResponse? = null,
    val lessonProgressSummaryResponses: List<LessonProgressSummaryResponses> = emptyList()
)
data class UnitProgressDetailResponse(
    val unitId: Int,
    val name: String,
    val totalLesson: Int,
    val completedLesson: Int
)
data class LessonProgressSummaryResponses(
    val lessonId: Int,
    val name: String,
    val isCompleted: Boolean
)

//레슨
data class LessonResponse(
    val problems: List<Problems> = emptyList(),
    val totalProblems: Int
)
data class Problems(
    val problemId: Int,
    val problemType: String,
    val question: String,
    val content: String,
    val answer: String,
    val options: List<OptionDto>,
)
data class OptionDto(
    val optionId: Int,
    val content: String,
    val explanation: String,
    val isAnswer: Boolean,
    val problemId: Int
)
data class ProblemResultItem(
    val problemId: Int,
    val isCorrect: Boolean,
    val incorrectCounts: Int
)
data class LessonResultRequest(
    val chapterId: Int,
    val unitId: Int,
    val lessonId: Int,
    val problemResults: List<ProblemResultItem>
)

//사용자
data class UserPageResponse(
    val nickname: String,
    val profileImgNumber: Int,
    val handle: String,
    val follower: Int,
    val following: Int
)

//리그
data class LeaguePageResponse<T>(
    val hasNextPage: Boolean,
    val contents: List<T>
)
data class LeagueItem(
    val rank: Int,
    val userId: Int,
    val lp: Int,
    val nickname: String,
    val profileImgNumber: Int,
    val xp: Int,
    val level: Int
)
data class MyLeague(
    val leagueId: Int,
    val leagueName: String,
    val rank: Int,
    val userId: Int,
    val lp: Int,
    val maxLp: Int,
    val nickname: String,
    val profileImgNumber: Int,
    val xp: Int,
    val level: Int
)
data class SeasonPopupResponse(
    val containsPopup: Boolean,
    val currentSeason: CurrentSeason,
    val lastSeasonPopupDto: LastSeasonPopupDto?
)
data class CurrentSeason(
    val nowSeason: String
)
data class LastSeasonPopupDto(
    val rank: Int,
    val leagueName: String,
    val profileImgNumber: Int
)
//친구
data class FriendItem(
    val id: Int,
    val nickname: String,
    val profileImgNumber: Int,
    val handle: String
)

data class FriendSearchResponse(
    val page: Int,
    val size: Int,
    val total: Int,
    val hasNext: Boolean,
    val searchUsers: List<FriendUser>
)

data class FriendUser(
    val userId: Long,
    val profileImgNumber: Int,
    val nickname: String,
    val handle: String,
    val isFollowing: Boolean
)

interface ApiService {
    @POST("api/v1/oauth/android")
    suspend fun sendCode(
        @Body token: IdTokenRequest
    ): AuthTokenResponse

    @POST("api/v1/users/me/onboarding")
    suspend fun completeOnboarding(
        @Body body: OnboardingRequest,
        @Header("Authorization") auth: String
    ): OnboardingResponse

    @GET("api/v1/main")
    suspend fun getMainPage(
        @Header("Authorization") auth: String,
    ): MainPageResponse

    @GET("api/v1/learning/chapters")
    suspend fun getChapterPage(
        @Header("Authorization") auth: String
    ): List<ChapterPageResponse>

    @GET("api/v1/learning/{chapterId}/units")
    suspend fun getUnitPage(
        @Header("Authorization") auth: String,
        @Path("chapterId") chapterId: Int
    ): List<UnitPageResponse>

    @GET("api/v1/learning/{lessonId}")
    suspend fun getLesson(
        @Header("Authorization") auth: String,
        @Path("lessonId") lessonId: Int
    ) : LessonResponse

    @POST("api/v1/learning/results")
    suspend fun sendResults(
        @Body body: LessonResultRequest,
        @Header("Authorization") auth: String
    )

    @GET("api/v1/users/my-page")
    suspend fun getUser(
        @Header("Authorization") auth: String
    ) : UserPageResponse

    @GET("api/v1/ranking/user-leagues/page/{pageNum}")
    suspend fun getLeagues_league(
        @Header("Authorization") auth: String,
        @Path("pageNum") pageNum: Int
    ) : LeaguePageResponse<LeagueItem>

    @GET("api/v1/ranking/leagues/{leagueId}/page/{pageNum}")
    suspend fun getLeagues_tier(
        @Header("Authorization") auth: String,
        @Path("leagueId") leagueId: Int,
        @Path("pageNum") pageNum: Int
    ) : LeaguePageResponse<LeagueItem>

    @GET("api/v1/ranking/me")
    suspend fun getMyLeague(
        @Header("Authorization") auth: String,
    ) : MyLeague

    @GET("api/v1/league/home")
    suspend fun getLeagueHome(
        @Header("Authorization") auth: String,
    ) : SeasonPopupResponse

    @GET("api/v1/friends/follower")
    suspend fun getFollower(
        @Header("Authorization") auth: String
    ) : List<FriendItem>

    @GET("api/v1/friends/following")
    suspend fun getFollowing(
        @Header("Authorization") auth: String
    ) : List<FriendItem>

    @POST("api/v1/friends/following/{followeeId}")
    suspend fun sendFolloweeId (
        @Header("Authorization") auth: String,
        @Path("followeeId") followeeId: Long
    )

    @POST("api/v1/friends/unfollowing/{followeeId}")
    suspend fun sendUnFolloweeId (
        @Header("Authorization") auth: String,
        @Path("followeeId") followeeId: Long
    )

    @GET("api/v1/friends/search")
    suspend fun getFriends(
        @Header("Authorization") auth: String,
        @Query("queryText") queryText: String,
        @Query("page") page: Int = 0
    ) : FriendSearchResponse
}

