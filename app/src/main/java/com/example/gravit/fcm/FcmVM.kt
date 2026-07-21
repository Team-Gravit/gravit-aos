package com.inuappcenter.gravit.fcm

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.FCMTokenRequest
import com.inuappcenter.gravit.error.handleApiFailure
import com.inuappcenter.gravit.fcm.DeviceIdManager.getDeviceId
import com.inuappcenter.gravit.fcm.DeviceIdManager.retryGetDeviceId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FcmViewModel(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data object Success : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()


    private val TAG = "FCM_TOKEN"
    private val MAX_BAD_REQUEST_COUNT = 3
    private val MAX_SERVER_ERROR_COUNT = 5
    private val INITIAL_RETRY_DELAY = 2_000L

    fun registerFcmToken() {
        viewModelScope.launch {
            _state.value = UiState.Loading

            val session = AuthPrefs.load(appContext)

            if (session == null) {
                _state.value = UiState.SessionExpired
                return@launch
            }

            var badRequestCount = 0
            var serverErrorCount = 0

            while (true){
                try {
                    var deviceId = getDeviceId(appContext)
                    var fcmToken = getFcmToken()

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
                            _state.value = UiState.Success
                            return@launch
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
                                _state.value = UiState.Failed
                                return@launch
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
                                _state.value = UiState.Failed
                                return@launch
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
                                _state.value = UiState.Failed
                                return@launch
                            }

                            val retryDelay =
                                INITIAL_RETRY_DELAY * (1L shl (serverErrorCount - 1))

                            Log.d(TAG, "${retryDelay}ms 후 재시도")
                            delay(retryDelay)
                        }

                        response.code() == 401 -> {
                            Log.e(TAG, "FCM 토큰 등록 중 세션 만료")

                            AuthPrefs.clear(appContext)
                            _state.value = UiState.SessionExpired
                            return@launch
                        }

                        response.code() == 404 -> {
                            _state.value = UiState.NotFound
                            return@launch
                        }

                        else -> {
                            Log.e(
                                TAG,
                                "HTTP 오류: ${response.code()}, " +
                                        "errorBody=${response.errorBody()?.string()}"
                            )

                            _state.value = UiState.Failed
                            return@launch
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: IOException) {
                    serverErrorCount++
                    Log.e(TAG, "네트워크 오류 발생: count=$serverErrorCount/$MAX_SERVER_ERROR_COUNT", e)

                    if (serverErrorCount >= MAX_SERVER_ERROR_COUNT) {
                        Log.e(TAG, "네트워크 오류 최대 시도 횟수 초과")
                        _state.value = UiState.Failed
                        return@launch
                    }

                    val retryDelay = INITIAL_RETRY_DELAY * (1L shl (serverErrorCount - 1))
                    Log.d(TAG, "네트워크 오류로 ${retryDelay}ms 후 재시도")
                    delay(retryDelay)
                } catch (e: Exception) {
                    Log.e(TAG, "FCM 토큰 등록 중 알 수 없는 오류", e)
                    _state.value = UiState.Failed
                    return@launch
                }
            }
        }
    }

    sealed interface ExistsUiState {
        data object Idle : ExistsUiState
        data object Loading : ExistsUiState
        data object Success : ExistsUiState
        data object Failed : ExistsUiState
        data object SessionExpired : ExistsUiState
        data object NotFound : ExistsUiState
    }

    private val _existsState = MutableStateFlow<ExistsUiState>(ExistsUiState.Idle)
    val existsState: StateFlow<ExistsUiState> = _existsState.asStateFlow()

    fun existsFCMToken() = viewModelScope.launch {
        _existsState.value = ExistsUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            _existsState.value = ExistsUiState.SessionExpired
            return@launch
        }
        val access = "Bearer ${session.accessToken}"
        val deviceId = getDeviceId(appContext)

         runCatching {
             api.getFCMExists(access, deviceId)
         }.onSuccess { res ->
             _existsState.value = ExistsUiState.Success
             if(!res.registered)
                 registerFcmToken()
         }.onFailure { e ->
             handleApiFailure(
                 e = e,
                 appContext = appContext,
                 onStateChange = { _existsState.value = it },
                 unauthorizedState = ExistsUiState.SessionExpired,
                 notFoundState = ExistsUiState.NotFound,
                 failedState = ExistsUiState.Failed
             )
         }
    }
}

private suspend fun getFcmToken(): String =
    suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!continuation.isActive) {
                    return@addOnCompleteListener
                }
                if (!task.isSuccessful) {
                    Log.w(
                        "FCM 토큰 조회 실패",
                        task.exception
                    )
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

@Suppress("UNCHECKED_CAST")
class FCMVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FcmViewModel(api, context.applicationContext) as T
    }
}
