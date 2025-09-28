package com.example.gravit.error

import android.content.Context
import retrofit2.HttpException
import com.example.gravit.api.AuthPrefs

inline fun <T> handleApiFailure(
    e: Throwable,
    appContext: Context,
    crossinline onStateChange: (T) -> Unit,
    unauthorizedState: T,
    notFoundState: T,
    failedState: T
) {
    val code = (e as? HttpException)?.code()
    when (code) {
        401 -> {
            AuthPrefs.clear(appContext)
            onStateChange(unauthorizedState)
        }
        404 -> onStateChange(notFoundState)
        else -> onStateChange(failedState)
    }
}