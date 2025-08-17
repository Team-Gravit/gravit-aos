package com.example.gravit.main.Chapter.Lesson

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.LessonResponse
import com.example.gravit.api.LessonResultRequest
import com.example.gravit.api.ProblemResultItem
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
            val code = (e as? retrofit2.HttpException)?.code()
            if (code == 401) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
            } else {
                _state.value = UiState.Failed
            }
        }
    }
    fun submitResults(
        chapterId: Int,
        unitId: Int,
        lessonId: Int,
        results: List<ProblemResultItem>,
        onDone: (Boolean) -> Unit = {}
    ) = viewModelScope.launch {
        val session = AuthPrefs.load(appContext)
        if (session == null || AuthPrefs.isExpired(session)) {
            AuthPrefs.clear(appContext)
            _state.value = UiState.SessionExpired
            onDone(false)
            return@launch
        }
        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.sendResults(
                LessonResultRequest(
                    chapterId = chapterId,
                    unitId = unitId,
                    lessonId = lessonId,
                    problemResults = results
                ),
                auth,
            )
        }.onSuccess { onDone(true) }
            .onFailure { onDone(false) }
    }
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