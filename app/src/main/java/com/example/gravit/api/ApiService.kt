package com.example.gravit.api

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response

//로그인
data class IdTokenRequest(val idToken: String)
data class AuthTokenResponse(val accessToken: String, val isOnboarded : Boolean)

//온보딩
data class OnboardingRequest(val nickname: String, val profilePhotoNumber: Int)
data class OnboardingResponse(val userId: Int, val profileImgNumber: Int, val nickname: String, val providerId: String)

//챕터
data class ChapterPageResponse(
    val chapterSummary: ChapterSummary,
    val chapterProgressRate: String
)
data class ChapterSummary(
    val chapterId: Int,
    val title: String,
    val description: String,
)

//홈
data class MainPageResponse(
    val nickname: String,
    val leagueName: String,
    val userLevelDetail: UserLevelDetail,
    val learningDetail: LearningDetail,
    val missionDetail: MissionDetail
)
data class UserLevelDetail(
    val xp: Int,
    val level: Int,
    val levelRate: Float
)
data class LearningDetail(
    val consecutiveSolvedDays: Int,
    val planetConquestRate: Float,
    val recentSolvedChapterId: Int,
    val recentSolvedChapterTitle: String,
    val recentSolvedChapterDescription: String,
    val recentSolvedChapterProgressRate: String
)
data class MissionDetail(
    val missionDescription: String,
    val awardXp: Int,
    val isCompleted: Boolean
)

// 유닛
data class UnitPageResponse(
    val chapterSummary: ChapterSummary,
    val unitDetails: List<UnitDetail>
)

data class UnitDetail(
    @SerializedName("unitSummaries")
    val unitSummary: UnitSummary,
    val progressRate: Double
)

data class UnitSummary(
    val unitId: Int,
    val title: String,
    val description: String
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
    val lessonId: Int,
    val learningTime: Int,
    val accuracy: Int,
    val problemResults: List<ProblemResultItem>
)
data class LessonResultResponse(
    val currentLevel: Int,
    val nextLevel: Int,
    val xp: Int
)

//신고
data class ReportRequest(
    val reportType: String,
    val content: String,
    val problemId: Int
)

//사용자
data class UserPageResponse(
    val nickname: String,
    val profileImgNumber: Int,
    val handle: String,
    val follower: Int,
    val following: Int
)

data class UserInfoResponse(
    val userId: Int,
    val profileImgNumber: Int,
    val nickname: String,
    val providerId: String
)

data class UpdateUserInfoRequest(
    val profilePhotoNumber: Int,
    val nickname: String,
)

//공지
data class NoticeDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val pinned: Boolean,
    val status: String,
    val publishedAt: String,
    val createdAt: String,
    val updatedAt: String
)

data class NoticeSummaryItem(
    val id: Long,
    val title: String,
    val summary: String,
    val pinned: Boolean,
    val publishedAt: String
)

data class NoticeSummaryPageResponse(
    val page: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    @SerializedName("contents") val content: List<NoticeSummaryItem>
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

data class SlicePage<T>(
    val hasNextPage: Boolean,
    val contents: List<T>
)

data class FriendItem(
    val id: Long,
    val nickname: String,
    val profileImgNumber: Int,
    val handle: String
)

data class FriendUser(
    val userId: Long,
    val profileImgNumber: Int,
    val nickname: String,
    val handle: String,
    val isFollowing: Boolean = false
)

data class FollowActionResponse(
    val followeeId: Long,
    val followerId: Long
)

data class FriendSearchResponse(
    val page: Int,
    val size: Int,
    val total: Int,
    val hasNext: Boolean,
    @SerializedName("contents")
    val contents: List<FriendUser>
)

data class Badges(
    val earnedCount: Int,
    val totalCount: Int,
    val badgeCategoryResponses: List<BadgeCategoryResponses>
)
data class BadgeCategoryResponses(
    val categoryId: Int,
    val categoryName: String,
    val order: Int,
    val categoryDescription: String,
    val badgeResponses: List<BadgeResponses>
)
data class BadgeResponses(
    val badgeId: Int,
    val code: String,
    val name: String,
    val description: String,
    val order: Int,
    val iconId: Int,
    val earned: Boolean
)

interface ApiService {
    @POST("api/v1/oauth/android")
    suspend fun sendCode(
        @Body token: IdTokenRequest
    ): AuthTokenResponse

    @POST("api/v1/users/onboarding")
    suspend fun completeOnboarding(
        @Body body: OnboardingRequest,
        @Header("Authorization") auth: String
    ): OnboardingResponse

    @GET("api/v1/users/main-page")
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
    ): UnitPageResponse

    @GET("api/v1/learning/{lessonId}")
    suspend fun getLesson(
        @Header("Authorization") auth: String,
        @Path("lessonId") lessonId: Int
    ) : LessonResponse

    @POST("api/v1/learning/results")
    suspend fun sendResults(
        @Body body: LessonResultRequest,
        @Header("Authorization") auth: String
    ) : LessonResultResponse

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

    @POST("api/v1/learning/reports")
    suspend fun sendReport(
        @Body body: ReportRequest,
        @Header("Authorization") auth: String,
    ) : ReportRequest

    @GET("api/v1/users")
    suspend fun userInfo(
        @Header("Authorization") auth: String
    ): Response<UserInfoResponse>

    @PATCH("api/v1/users")
    suspend fun updateUserInfo(
        @Header("Authorization") auth: String,
        @Body body: UpdateUserInfoRequest
    ): Response<UserInfoResponse>

    @GET("api/v1/notice/{noticeId}")
    suspend fun getNoticeDetail(
        @Header("Authorization") auth: String,
        @Path("noticeId") noticeId: Long
    ): NoticeDetailResponse

    @GET("api/v1/notice/summaries/{page}")
    suspend fun getNoticeSummaries(
        @Header("Authorization") auth: String,
        @Path("page") page: Int
    ): NoticeSummaryPageResponse

    @GET("api/v1/badges/me")
    suspend fun getBadges(
        @Header("Authorization") auth: String,
    ) : Badges

    @POST("api/v1/users/deletion/request")
    suspend fun requestDeletionMail(
        @Header("Authorization") auth: String,
        @Query("dest") dest: String,
    ): Response<Unit>

    @GET("api/v1/friends/following")
    suspend fun getFollowing(
        @Header("Authorization") auth: String,
        @Query("page") page: Int = 0
    ): SlicePage<FriendItem>

    @GET("api/v1/friends/follower")
    suspend fun getFollower(
        @Header("Authorization") auth: String,
        @Query("page") page: Int = 0
    ): SlicePage<FriendItem>

    @POST("api/v1/friends/following/{followeeId}")
    suspend fun sendFolloweeId(
        @Header("Authorization") auth: String,
        @Path("followeeId") followeeId: Long
    ): retrofit2.Response<FollowActionResponse>

    @POST("api/v1/friends/unfollowing/{followeeId}")
    suspend fun sendUnFolloweeId(
        @Header("Authorization") auth: String,
        @Path("followeeId") followeeId: Long
    ): retrofit2.Response<Unit>

    @GET("api/v1/friends/search")
    suspend fun getFriends(
        @Header("Authorization") auth: String,
        @Query("queryText") queryText: String,
        @Query("page") page: Int = 0
    ): SlicePage<FriendUser>

    @GET("api/v1/cs-notes/{chapter}/{unit}")
    suspend fun getNotes(
        @Header("Authorization") auth: String,
        @Path("chapter") chapter: String,
        @Path("unit") unit: String
    ): ResponseBody
}

