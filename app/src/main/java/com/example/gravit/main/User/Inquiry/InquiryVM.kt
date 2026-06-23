package com.example.gravit.main.User.Inquiry

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.InquiryDetail
import com.inuappcenter.gravit.api.InquiryListResponses
import com.inuappcenter.gravit.api.InquiryRequest
import com.inuappcenter.gravit.error.handleApiFailure
import com.inuappcenter.gravit.main.User.UserScreenVM
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
                AuthPrefs.setOnboarded(appContext, true)
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
    private var page = 1
    private var hasNext = true
    private var isLoading = false

    private val _loadState = MutableStateFlow<LoadUiState>(LoadUiState.Idle)
    val loadState = _loadState.asStateFlow()

    fun loadInquiryList() = viewModelScope.launch {
        if (isLoading) return@launch

        isLoading = true
        _loadState.value = LoadUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _loadState.value = LoadUiState.SessionExpired
            return@launch
        }
        runCatching {
           api.getInquiry("Bearer ${session.accessToken}", 1)
        }.onSuccess { res ->
            page = 1
            hasNext = res.hasNext

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
    }

    fun loadMoreInquiryList() = viewModelScope.launch {
        if (isLoading || !hasNext) return@launch

        val currentState = _loadState.value as? LoadUiState.Success ?: return@launch

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _loadState.value = LoadUiState.SessionExpired
            return@launch
        }

        isLoading = true

        try {
            runCatching {
                api.getInquiry("Bearer ${session.accessToken}", page + 1)
            }.onSuccess { next ->
                page += 1
                hasNext = next.hasNext

                _loadState.value = currentState.copy(
                    inquiryList = currentState.inquiryList.copy(
                        contents = currentState.inquiryList.contents + next.contents,
                        hasNext = next.hasNext
                    )
                )
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

    private val _inquiryDetailState = MutableStateFlow<InquiryDetailUiState>(InquiryDetailUiState.Idle)
    val inquiryDetailState = _inquiryDetailState.asStateFlow()

    fun loadInquiryDetail(inquiryId: Long) = viewModelScope.launch {
        _inquiryDetailState.value = InquiryDetailUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _inquiryDetailState.value = InquiryDetailUiState.SessionExpired
            return@launch
        }
        runCatching {
            api.getInquiryDetail("Bearer ${session.accessToken}", inquiryId)
        }.onSuccess { res ->
            _inquiryDetailState.value = InquiryDetailUiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _inquiryDetailState.value = it },
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