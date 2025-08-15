package com.example.gravit.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

data class IdTokenRequest(val idToken: String)
data class AuthTokenResponse(val accessToken: String, val isOnboarded : Boolean)

data class OnboardingRequest(val nickname: String, val profilePhotoNumber: Int)
data class OnboardingResponse(val userId: Int, val profileImgNumber: Int, val nickname: String, val providerId: String)

data class MainPageResponse(
    val nickname: String,
    val level: Int,
    val xp: Int,
    val league: String,
    val recentLearningSummaryResponse: RecentLearningSummaryResponse? = null
)

data class RecentLearningSummaryResponse(
    val chapterId: Long,
    val chapterName: String,
    val totalUnits: Int,
    val completedUnits: Int
)

interface ApiService {
    @POST("api/v1/oauth/android")
    suspend fun sendCode(
        @Body token: IdTokenRequest
    ): AuthTokenResponse

    @PATCH("api/v1/users/onboarding")
    suspend fun completeOnboarding(
        @Body body: OnboardingRequest,
        @Header("Authorization") auth: String
    ): OnboardingResponse

    @GET("api/v1/main")
    suspend fun getMainPage(
        @Header("Authorization") auth: String,
    ): MainPageResponse
}

