package com.example.gravit.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

data class IdTokenRequest(val idToken: String)
data class AuthTokenResponse(val accessToken: String, val isOnboarded : Boolean)

data class OnboardingRequest(val nickname: String, val profilePhotoNumber: Int)
data class OnboardingResponse(val userId: Int, val profilePhotoNumber: Int, val nickname: String, val providerId: String)

interface ApiService {
    @POST("api/v1/oauth/android")
    suspend fun sendCode(
        @Body token: IdTokenRequest
    ): AuthTokenResponse

    @PATCH("api/v1/users/onboarding")
    suspend fun completeOnboarding(
        @Body body: OnboardingRequest,
        @Header("Authorization") auth: String // "Bearer <accessToken>"
    ): OnboardingResponse
}

