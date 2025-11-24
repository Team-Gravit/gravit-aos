package com.example.gravit.main.Chapter.Lesson

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.LessonResponse
import com.example.gravit.api.LessonResultRequest
import com.example.gravit.api.LessonResultResponse
import com.example.gravit.api.ProblemResultItem
import com.example.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonViewModel(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState{
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val data: LessonResponse) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(lessonId: Int) = viewModelScope.launch {
        _state.value = UiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.getLesson(auth, lessonId)
        }.onSuccess { res ->
            _state.value = UiState.Success(res)
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _state.value = it },
                unauthorizedState = UiState.SessionExpired,
                notFoundState = UiState.NotFound,
                failedState = UiState.Failed
            )
        }
    }
    sealed interface SubmitState{
        data object Idle : SubmitState
        data object Loading : SubmitState
        data class Success(val data: LessonResultResponse) : SubmitState
        data object Failed : SubmitState
        data object SessionExpired : SubmitState
        data object NotFound : SubmitState
    }
    private val _submit = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submit = _submit.asStateFlow()
    fun submitResults(
        lessonId: Int,
        learningTime: Int,
        accuracy: Int,
        results: List<ProblemResultItem>,
        onDone: (Boolean) -> Unit = {}
    ) = viewModelScope.launch {
        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _submit.value = SubmitState.SessionExpired
            onDone(false)
            return@launch
        }
        val auth = "Bearer ${session.accessToken}"
        runCatching {
            val request = LessonResultRequest(lessonId, learningTime, accuracy, results)
           api.sendResults(request, auth)
        }.onSuccess { response ->
            _submit.value = SubmitState.Success(response)
            onDone(true)
        }.onFailure { e ->
            onDone(false)
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _submit.value = it },
                unauthorizedState = SubmitState.SessionExpired,
                notFoundState = SubmitState.NotFound,
                failedState = SubmitState.Failed
            )
        }
    }

    fun resetSubmit() { _submit.value = SubmitState.Idle }
}

@Suppress("UNCHECKED_CAST")
class LessonVMFactory(
    private val api: ApiService,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LessonViewModel(api, context.applicationContext) as T
    }
}