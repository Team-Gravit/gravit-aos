package com.example.gravit.main.User

import android.content.Context
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.NoticeDetailResponse
import com.example.gravit.api.NoticeSummaryPageResponse
import com.example.gravit.api.RetrofitInstance

class NoticeRepository(private val appContext: Context) {

    private fun bearerOrThrow(): String {
        val token = AuthPrefs.load(appContext)?.accessToken
            ?: throw IllegalStateException("세션 만료")
        return "Bearer $token"
    }

    suspend fun fetchSummaries(page: Int): NoticeSummaryPageResponse {
        return RetrofitInstance.api.getNoticeSummaries(bearerOrThrow(), page)
    }

    suspend fun fetchDetail(id: Long): NoticeDetailResponse {
        return RetrofitInstance.api.getNoticeDetail(bearerOrThrow(), id)
    }
}
