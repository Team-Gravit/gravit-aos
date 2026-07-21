package com.inuappcenter.gravit.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.FCMTokenRequest
import com.inuappcenter.gravit.fcm.DeviceIdManager.getDeviceId
import com.inuappcenter.gravit.fcm.DeviceIdManager.retryGetDeviceId
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FcmManager(
    private val api: ApiService,
    private val context: Context
) {
    private val appContext = context.applicationContext
    companion object {
        private const val TAG = "FCM_TOKEN"
        private const val MAX_BAD_REQUEST_COUNT = 3
        private const val MAX_SERVER_ERROR_COUNT = 5
        private const val INITIAL_RETRY_DELAY = 2_000L
    }

    suspend fun checkAndRegister() {
        val session = AuthPrefs.load(appContext) ?: return
        val deviceId = getDeviceId(appContext)

        runCatching {
            api.getFCMExists(
                auth = "Bearer ${session.accessToken}",
                deviceId = deviceId
            )
        }.onSuccess { result ->
            if (!result.registered) {
                register()
            }
        }.onFailure {
            Log.e("FCM_TOKEN", "FCM 등록 여부 확인 실패", it)
        }
    }

    suspend fun register(newToken: String? = null) {
        val session = AuthPrefs.load(appContext) ?: return
        var deviceId = getDeviceId(appContext)
        var fcmToken = newToken ?: getFcmToken()

        var badRequestCount = 0
        var serverErrorCount = 0

        while (true){
            try {
                val response = api.sendFCMToken(
                    auth = "Bearer ${session.accessToken}",
                    request = FCMTokenRequest(
                        deviceId = deviceId,
                        fcmToken = fcmToken,
                        platform = "ANDROID"
                    )
                )
                when {
                    response.isSuccessful -> {
                        Log.d(TAG, "FCM 토큰 서버 등록 성공")
                        return
                    }

                    response.code() == 400 -> {
                        badRequestCount++
                        val errorBody = response.errorBody()?.string()

                        Log.e(
                            TAG,
                            """
                                FCM 토큰 등록 400 발생
                                errorBody: $errorBody
                                400 count: $badRequestCount/$MAX_BAD_REQUEST_COUNT
                                deviceId is null: ${false}
                                deviceId is blank: ${deviceId.isBlank()}
                                token is null: ${false}
                                token is blank: ${fcmToken.isBlank()}
                                """.trimIndent()
                        )

                        if (badRequestCount >= MAX_BAD_REQUEST_COUNT) {
                            Log.e(TAG, "400 최대 시도 횟수 초과")
                            return
                        }

                        val previousDeviceId = deviceId
                        val previousToken = fcmToken

                        deviceId = retryGetDeviceId(appContext)
                        fcmToken = getFcmToken()

                        Log.d(
                            TAG,
                            """
                                400 대응 값 갱신 완료
                                deviceId 갱신 성공: ${deviceId.isNotBlank()}
                                이전 deviceId와 동일: ${previousDeviceId == deviceId}
                                token 재조회 성공: ${fcmToken.isNotBlank()}
                                이전 token과 동일: ${previousToken == fcmToken}
                                """.trimIndent()
                        )

                        if (deviceId.isBlank() || fcmToken.isBlank()) {
                            Log.e(TAG, "400 대응 중 deviceId 또는 token 갱신 실패")
                            return
                        }
                        delay(INITIAL_RETRY_DELAY)
                    }

                    response.code() == 500 -> {
                        serverErrorCount++

                        Log.e(
                            TAG,
                            "서버 오류 발생: ${response.code()}, " +
                                    "count=$serverErrorCount/$MAX_SERVER_ERROR_COUNT"
                        )

                        if (serverErrorCount >= MAX_SERVER_ERROR_COUNT) {
                            Log.e(TAG, "서버 오류 최대 시도 횟수 초과")
                            return
                        }

                        val retryDelay =
                            INITIAL_RETRY_DELAY * (1L shl (serverErrorCount - 1))

                        Log.d(TAG, "${retryDelay}ms 후 재시도")
                        delay(retryDelay)
                    }

                    else -> {
                        Log.e(
                            TAG,
                            "HTTP 오류: ${response.code()}, " +
                                    "errorBody=${response.errorBody()?.string()}"
                        )
                        return
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                serverErrorCount++
                Log.e(TAG, "네트워크 오류 발생: count=$serverErrorCount/$MAX_SERVER_ERROR_COUNT", e)

                if (serverErrorCount >= MAX_SERVER_ERROR_COUNT) {
                    Log.e(TAG, "네트워크 오류 최대 시도 횟수 초과")
                    return
                }

                val retryDelay = INITIAL_RETRY_DELAY * (1L shl (serverErrorCount - 1))
                Log.d(TAG, "네트워크 오류로 ${retryDelay}ms 후 재시도")
                delay(retryDelay)
            } catch (e: Exception) {
                Log.e(TAG, "FCM 토큰 등록 중 알 수 없는 오류", e)
                return
            }
        }
    }

    suspend fun getFcmToken(): String =
        suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!continuation.isActive) {
                        return@addOnCompleteListener
                    }
                    if (!task.isSuccessful) {
                        continuation.resumeWithException(
                            task.exception
                                ?: IllegalStateException("FCM 토큰 조회 실패")
                        )
                        return@addOnCompleteListener
                    }
                    val token = task.result
                    continuation.resume(token)
                }
        }

}