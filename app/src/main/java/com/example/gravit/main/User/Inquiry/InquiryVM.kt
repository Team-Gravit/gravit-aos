package com.inuappcenter.gravit.main.User.Inquiry

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.InquiryDetail
import com.inuappcenter.gravit.api.InquiryListResponses
import com.inuappcenter.gravit.api.InquiryRequest
import com.inuappcenter.gravit.error.handleApiFailure
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InquiryVM (
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

    private val _submitState = MutableStateFlow<UiState>(UiState.Idle)
    val submitState = _submitState.asStateFlow()

    fun submit(title: String, type: String, content: String) {
        viewModelScope.launch {
            _submitState.value = UiState.Loading

            val session = AuthPrefs.load(appContext)
            if (session == null) {
                AuthPrefs.clear(appContext)
                _submitState.value = UiState.SessionExpired
                return@launch
            }

            runCatching {
                api.sendInquiry("Bearer ${session.accessToken}", InquiryRequest(title, type, content))
            }.onSuccess {
                _submitState.value = UiState.Success
            }.onFailure { e ->
                handleApiFailure(
                    e = e,
                    appContext = appContext,
                    onStateChange = { _submitState.value = it },
                    unauthorizedState = UiState.SessionExpired,
                    notFoundState = UiState.NotFound,
                    failedState = UiState.Failed
                )
            }
        }
    }
    sealed interface  LoadUiState {
        data object Idle : LoadUiState
        data object Loading : LoadUiState
        data class Success(val inquiryList: InquiryListResponses) : LoadUiState
        data object Failed : LoadUiState
        data object SessionExpired : LoadUiState
        data object NotFound : LoadUiState
    }
    private var isLoading = false
    private var pendingPage: Int? = null

    private val _loadState = MutableStateFlow<LoadUiState>(LoadUiState.Idle)
    val loadState = _loadState.asStateFlow()

    fun loadInquiryList(page: Int = 1): Job = viewModelScope.launch {
        if (isLoading) {
            pendingPage = page
            return@launch
        }

        isLoading = true
        _loadState.value = LoadUiState.Loading

        try {
            val session = AuthPrefs.load(appContext)

            if (session == null) {
                AuthPrefs.clear(appContext)
                _loadState.value = LoadUiState.SessionExpired
                return@launch
            }

            runCatching {
                api.getInquiry(
                    "Bearer ${session.accessToken}",
                    page
                )
            }.onSuccess { res ->
                _loadState.value = LoadUiState.Success(res)
            }.onFailure { e ->
                handleApiFailure(
                    e = e,
                    appContext = appContext,
                    onStateChange = { _loadState.value = it },
                    unauthorizedState = LoadUiState.SessionExpired,
                    notFoundState = LoadUiState.NotFound,
                    failedState = LoadUiState.Failed
                )
            }
        } finally {
            isLoading = false

            // 로딩 중 요청된 마지막 페이지 로드
            pendingPage?.let { nextPage ->
                pendingPage = null

                if (nextPage != page) {
                    loadInquiryList(nextPage)
                }
            }
        }
    }


    sealed interface  InquiryDetailUiState {
        data object Idle : InquiryDetailUiState
        data object Loading : InquiryDetailUiState
        data class Success(val inquiry: InquiryDetail) : InquiryDetailUiState
        data object Failed : InquiryDetailUiState
        data object SessionExpired : InquiryDetailUiState
        data object NotFound : InquiryDetailUiState
    }

    private val _inquiryDetailStates = MutableStateFlow<Map<Long, InquiryDetailUiState>>(emptyMap())
    val inquiryDetailStates = _inquiryDetailStates.asStateFlow()
    fun loadInquiryDetail(inquiryId: Long) = viewModelScope.launch {
        _inquiryDetailStates.value += (inquiryId to InquiryDetailUiState.Loading)
        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _inquiryDetailStates.value += (inquiryId to InquiryDetailUiState.SessionExpired)
            return@launch
        }
        runCatching {
            api.getInquiryDetail(
                "Bearer ${session.accessToken}",
                inquiryId
            )
        }.onSuccess { res ->
            _inquiryDetailStates.value += (inquiryId to InquiryDetailUiState.Success(res))
        }.onFailure { e ->

            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { state -> _inquiryDetailStates.value += (inquiryId to state) },
                unauthorizedState = InquiryDetailUiState.SessionExpired,
                notFoundState = InquiryDetailUiState.NotFound,
                failedState = InquiryDetailUiState.Failed
            )
        }
    }

    fun resetSubmitState() {
        _submitState.value = UiState.Idle
    }
}

@Suppress("UNCHECKED_CAST")
class InquiryVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InquiryVM(api, context.applicationContext) as T
    }
}