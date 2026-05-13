package com.inuappcenter.gravit.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.Response
import retrofit2.http.HTTP

//로그인
data class IdTokenRequest(val idToken: String)
data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val isOnboarded : Boolean
)
data class NaverUserInfo(
    val email: String,
    val providerId: String,
    val nickname: String
)

//리프레시
data class RefreshTokenRequest(
    val refreshToken: String
)
data class RefreshTokenResponse(
    val accessToken: String
)

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

//레슨리스트
data class LessonListResponse(
    val unitSummary: UnitSummary,
    val unitId: Int,
    val lessonSummaries: List<LessonSummaries>,
    val bookmarkAccessible: Boolean,
    val wrongAnsweredNoteAccessible: Boolean
)
data class LessonSummaries(
    val lessonId: Int,
    val title: String,
    val totalProblem: Int,
    val isSolved: Boolean
)

//문제
data class ProblemResponse(
    val unitSummary: UnitSummary,
    val problems: List<Problems>,
    val totalProblems: Int
)
data class Problems(
    val problemId: Int,
    val problemType: String,
    val instruction: String,
    val content: String,
    val answerResponse: AnswerResponse,
    val options: List<OptionDto>,
    val isBookmarked: Boolean,
)
data class AnswerResponse(
    val contents: List<String>,
    val explanation: String
)
data class OptionDto(
    val optionId: Int,
    val content: String,
    val explanation: String,
    val isAnswer: Boolean,
    val problemId: Int
)

//제출
data class LessonResultRequest(
    val lessonSubmissionSaveRequest: LessonSubmissionSaveRequest?,
    val problemSubmissionRequests: List<ProblemSubmissionRequests>?
)
data class LessonSubmissionSaveRequest(
    val lessonId: Int,
    val learningTime: Int,
    val accuracy: Float
)
@Parcelize
data class ProblemSubmissionRequests(
    val problemId: Int,
    val isCorrect: Boolean
) : Parcelable

//제출 결과
data class LessonResultResponse(
    val leagueName: String,
    val userLevelResponse: UserLevelResponse,
    val unitSummary: UnitSummary

)
data class UserLevelResponse(
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

//친구
data class FriendSearchResponse(
    val hasNextPage: Boolean,
    val contents: List<FriendItem>
)

data class FriendItem(
    val userId: Long,
    val profileImgNumber: Int,
    val nickname: String,
    val handle: String,
    val isFollowing: Boolean
)

data class FollowResponse(
    val followeeId: Long,
    val followerId: Long
)

data class FriendUserSummary(
    val id: Long,
    val nickname: String,
    val profileImgNumber: Int,
    val handle: String
)

data class FriendSliceResponse(
    val hasNextPage: Boolean,
    val contents: List<FriendUserSummary>
)

data class FriendCountResponse(
    val followerCount: Int,
    val followingCount: Int
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

//뱃지
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

//북마크
data class BookmarksRequest(
    val problemId: Int
)

interface ApiService {
    //OAuth2.0 Android API
    @POST("api/v1/oauth/android") //OAuth 회원가입/로그인 처리
    suspend fun sendCode(
        @Query("provider") provider: String,
        @Body token: IdTokenRequest
    ): AuthTokenResponse
    @POST("api/v1/oauth/android/naver") //네이버 OAuth 회원가입/로그인 처리
    suspend fun sendNaverInfo(
        @Body body: NaverUserInfo
    ) : AuthTokenResponse

    //User API
    @POST("api/v1/users/onboarding") //온보딩 정보 등록
    suspend fun completeOnboarding(
        @Body body: OnboardingRequest,
        @Header("Authorization") auth: String
    ): OnboardingResponse
    @GET("api/v1/users") //유저 정보 조회
    suspend fun userInfo(
        @Header("Authorization") auth: String
    ): Response<UserInfoResponse>
    @PATCH("api/v1/users") //프로필 수정
    suspend fun updateUserInfo(
        @Header("Authorization") auth: String,
        @Body body: UpdateUserInfoRequest
    ): Response<UserInfoResponse>
    @GET("api/v1/users/my-page") //마이페이지 조회
    suspend fun getUser(
        @Header("Authorization") auth: String
    ) : UserPageResponse
    @GET("api/v1/users/main-page") //메인 페이지 조회
    suspend fun getMainPage(
        @Header("Authorization") auth: String,
    ): MainPageResponse

    //Chapter API
    @GET("api/v1/chapters") //챕터 조회
    suspend fun getChapterPage(
        @Header("Authorization") auth: String
    ): List<ChapterPageResponse>

    //Unit API
    @GET("api/v1/units/{chapterId}") //유닛 조회
    suspend fun getUnitPage(
        @Header("Authorization") auth: String,
        @Path("chapterId") chapterId: Int
    ): UnitPageResponse

    //Problem API
    @GET("api/v1/problems/{lessonId}") //레슨 문제 조회
    suspend fun getLesson(
        @Header("Authorization") auth: String,
        @Path("lessonId") lessonId: Int
    ) : ProblemResponse
    @POST("api/v1/problems/results") //문제 결과 저장
    suspend fun sendProblemResults(
        @Body body: ProblemSubmissionRequests,
        @Header("Authorization") auth: String
    )

    //CS-Note API
    @GET("api/v1/cs-notes/{unitId}") //개념 노트 조회
    suspend fun getNotes(
        @Header("Authorization") auth: String,
        @Path("unitId") unitId: Int
    ): ResponseBody

    //Lesson API
    @GET("api/v1/lessons/{unitId}") //레슨 목록 조회
    suspend fun getLessonList(
        @Header("Authorization") auth: String,
        @Path("unitId") unit: Int
    ) : LessonListResponse
    @POST("api/v1/lessons/results") //레슨 결과 저장
    suspend fun sendLessonResults(
        @Body body: LessonResultRequest,
        @Header("Authorization") auth: String
    ) : LessonResultResponse

    //UserLeague API
    @GET("api/v1/ranking/user-leagues/page/{pageNum}") //내 리그 기준 유저 랭킹 조회
    suspend fun getLeagues_league(
        @Header("Authorization") auth: String,
        @Path("pageNum") pageNum: Int
    ) : LeaguePageResponse<LeagueItem>
    @GET("api/v1/ranking/leagues/{leagueId}/page/{pageNum}") //티어별 유저 랭킹 조회
    suspend fun getLeagues_tier(
        @Header("Authorization") auth: String,
        @Path("leagueId") leagueId: Int,
        @Path("pageNum") pageNum: Int
    ) : LeaguePageResponse<LeagueItem>
    @GET("api/v1/ranking/me") //내 리그·랭킹 요약 조회
    suspend fun getMyLeague(
        @Header("Authorization") auth: String,
    ) : MyLeague

    //League API
    @GET("api/v1/league/home") //리그 페이지 home 조회
    suspend fun getLeagueHome(
        @Header("Authorization") auth: String,
    ) : SeasonPopupResponse

    //Report API
    @POST("api/v1/reports") //문제 신고 제출
    suspend fun sendReport(
        @Body body: ReportRequest,
        @Header("Authorization") auth: String,
    ) : ReportRequest

    //Notice Query API
    @GET("api/v1/notice/{noticeId}") //공지 상세 조회
    suspend fun getNoticeDetail(
        @Header("Authorization") auth: String,
        @Path("noticeId") noticeId: Long
    ): NoticeDetailResponse
    @GET("api/v1/notice/summaries/{page}") //공지 요약 목록 조회
    suspend fun getNoticeSummaries(
        @Header("Authorization") auth: String,
        @Path("page") page: Int
    ): NoticeSummaryPageResponse

    //Badge API
    @GET("api/v1/badges/me") //내 뱃지 목록 조회
    suspend fun getBadges(
        @Header("Authorization") auth: String,
    ) : Badges

    //User Deletion API
    @POST("api/v1/users/deletion/request") //계정 삭제 요청
    suspend fun requestDeletionMail(
        @Header("Authorization") auth: String,
        @Query("dest") dest: String,
    ): Response<Unit>

    //Bookmark API
    @GET("api/v1/bookmarks/{unitId}") //유닛 내 북마크 된 문제 조회
    suspend fun getBookmarks(
        @Header("Authorization") auth: String,
        @Path("unitId") unitId: Int
    ) : ProblemResponse
    @POST("api/v1/bookmarks") //북마크 저장
    suspend fun addBookmark(
        @Header("Authorization") auth: String,
        @Body request: BookmarksRequest
    )
    @HTTP( //북마크 삭제
        method = "DELETE",
        path = "api/v1/bookmarks",
        hasBody = true
    )
    suspend fun removeBookmark(
        @Header("Authorization") auth: String,
        @Body request: BookmarksRequest
    )

    //WrongAnsweredNote API
    @GET("api/v1/wrong-answered-notes/{unitId}") //유닛 내 오답 문제 조회
    suspend fun getWrongAnswered(
        @Header("Authorization") auth: String,
        @Path("unitId") unitId: Int
    ) : ProblemResponse
    @HTTP( //오답노트 삭제
        method = "DELETE",
        path = "api/v1/wrong-answered-notes",
        hasBody = true
    )
    suspend fun removeWrongAnswered(
        @Header("Authorization") auth: String,
        @Body request: BookmarksRequest
    )

    //AuthToken API
    @POST("api/v1/auth/reissue") //리프레시 토큰
    suspend fun sendRefreshToken(
        @Body token: RefreshTokenRequest
    ) : RefreshTokenResponse

    //Friend API
    @POST("api/v1/friends/unfollowing/{followeeId}") //언팔로잉
    suspend fun unfollow(
        @Header("Authorization") auth: String,
        @Path("followeeId") followeeId: Long
    ): Response<Unit>
    @POST("api/v1/friends/reject-following/{followerId}") //팔로잉 거절
    suspend fun rejectFollowing(
        @Header("Authorization") auth: String,
        @Path("followerId") followerId: Long
    ): Response<Unit>
    @POST("api/v1/friends/following/{followeeId}") //팔로잉
    suspend fun follow(
        @Header("Authorization") auth: String,
        @Path("followeeId") followeeId: Long
    ): Response<FollowResponse>
    @GET("api/v1/friends/following") //팔로잉 목록 조회
    suspend fun getFollowingList(
        @Header("Authorization") auth: String,
        @Query("page") page: Int = 0
    ): Response<FriendSliceResponse>
    @GET("api/v1/friends/follower") //팔로워 목록 조회
    suspend fun getFollowerList(
        @Header("Authorization") auth: String,
        @Query("page") page: Int = 0
    ): Response<FriendSliceResponse>
    @GET("api/v1/friends/count") //팔로워/팔로잉 카운트 조회
    suspend fun getFriendCount(
        @Header("Authorization") auth: String
    ): Response<FriendCountResponse>
    @GET("api/v1/friends/search") //핸들&닉네임 검색
    suspend fun getFriends(
        @Header("Authorization") auth: String,
        @Query("queryText") queryText: String,
        @Query("page") page: Int
    ): Response<FriendSearchResponse>
}

