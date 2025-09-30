package com.example.gravit.error

import android.content.Context
import com.example.gravit.api.AuthPrefs
import retrofit2.HttpException

fun isDeletionPending(ctx: Context): Boolean =
    ctx.getSharedPreferences("acct_del", Context.MODE_PRIVATE)
        .getBoolean("pending", false)

inline fun <T> handleApiFailure(
    e: Throwable,
    appContext: Context,
    crossinline onStateChange: (T) -> Unit,
    unauthorizedState: T,
    notFoundState: T,
    failedState: T,
    accountDeletedWhenPendingState: T? = null,
    accountDeletedWhenNotPendingState: T? = null
) {
    val http = e as? HttpException
    if (http == null) { onStateChange(failedState); return }

    when (http.code()) {
        401 -> {
            AuthPrefs.clear(appContext)
            onStateChange(unauthorizedState)
        }
        404 -> {
            val body = try { http.response()?.errorBody()?.string().orEmpty() } catch (_: Throwable) { "" }
            val isUser4041 = body.contains("\"error\":\"USER_4041\"")

            if (isUser4041) {
                val pending = isDeletionPending(appContext)
                val mapped = if (pending) accountDeletedWhenPendingState else accountDeletedWhenNotPendingState
                if (mapped != null) {
                    onStateChange(mapped)
                    return
                }
            }
            onStateChange(notFoundState)
        }
        else -> onStateChange(failedState)
    }
}