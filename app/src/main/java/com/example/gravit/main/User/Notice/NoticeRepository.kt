package com.inuappcenter.gravit.main.User.Notice

import android.content.Context
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.NoticeDetailResponse
import com.inuappcenter.gravit.api.NoticeSummaryPageResponse
import com.inuappcenter.gravit.api.RetrofitInstance

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
