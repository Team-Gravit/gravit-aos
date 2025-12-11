package com.example.gravit.main.User.Notice

import android.content.Context
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.NoticeDetailResponse
import com.example.gravit.api.NoticeSummaryPageResponse
import com.example.gravit.api.RetrofitInstance

class NoticeRepository(private val appContext: Context) {

    private fun bearerOrFailure(): Result<String> {
        val token = AuthPrefs.load(appContext)?.accessToken
            ?: return Result.failure(IllegalStateException("세션 만료"))

        return Result.success("Bearer $token")
    }

    suspend fun fetchSummaries(page: Int): Result<NoticeSummaryPageResponse> {
        return bearerOrFailure().mapCatching { bearer ->
            RetrofitInstance.api.getNoticeSummaries(bearer, page)
        }
    }

    suspend fun fetchDetail(id: Long): Result<NoticeDetailResponse> {
        return bearerOrFailure().mapCatching { bearer ->
            RetrofitInstance.api.getNoticeDetail(bearer, id)
        }
    }
}
