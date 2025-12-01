package com.example.gravit.main.Study.Problem

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gravit.api.ApiService
import com.example.gravit.api.AuthPrefs
import com.example.gravit.api.BookmarksRequest
import com.example.gravit.api.LessonResultRequest
import com.example.gravit.api.LessonResultResponse
import com.example.gravit.api.LessonSubmissionSaveRequest
import com.example.gravit.api.ProblemResponse
import com.example.gravit.api.ProblemSubmissionRequests
import com.example.gravit.api.Problems
import com.example.gravit.error.handleApiFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.associate
import kotlin.coroutines.cancellation.CancellationException

class LessonViewModel(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface UiState{
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val data: ProblemResponse) : UiState
        data object Failed : UiState
        data object SessionExpired : UiState
        data object NotFound : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(lessonId: Int = 0, unitId: Int = 0, type: String) = viewModelScope.launch {
        try{
            _state.value = UiState.Loading
            val session = AuthPrefs.load(appContext)
            if (session == null) {
                AuthPrefs.clear(appContext)
                _state.value = UiState.SessionExpired
                return@launch
            }

            val auth = "Bearer ${session.accessToken}"
            runCatching {
                when(type) {
                    "bookmarks" ->
                        api.getBookmarks(auth, unitId)
                    "wrong-answered-notes" ->
                        api.getWrongAnswered(auth,unitId)
                    else -> api.getLesson(auth, lessonId)
                }
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _state.value = UiState.Failed
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
        lessonSubmissionSaveRequest: LessonSubmissionSaveRequest?,
        problemSubmissionRequests: List<ProblemSubmissionRequests>?,
        onDone: (Boolean) -> Unit = {}
    ) = viewModelScope.launch {

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _submit.value = SubmitState.SessionExpired
            onDone(false)
            return@launch
        }
        val auth = "Bearer ${session.accessToken}"
        runCatching {
            val result = LessonResultRequest(lessonSubmissionSaveRequest, problemSubmissionRequests)
           api.sendLessonResults(result, auth)
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

    sealed interface ProblemSubmitState{
        data object Idle : ProblemSubmitState
        data object Loading : ProblemSubmitState
        data object Success : ProblemSubmitState
        data object Failed : ProblemSubmitState
        data object SessionExpired : ProblemSubmitState
        data object NotFound : ProblemSubmitState
    }
    private val _problemSubmit = MutableStateFlow<ProblemSubmitState>(ProblemSubmitState.Idle)
    val problemSubmit = _problemSubmit.asStateFlow()
    fun submitProblemResults(
        problemSubmissionRequests: ProblemSubmissionRequests,
        onDone: (Boolean) -> Unit = {}
    ) = viewModelScope.launch {

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _problemSubmit.value = ProblemSubmitState.SessionExpired
            onDone(false)
            return@launch
        }
        val auth = "Bearer ${session.accessToken}"
        runCatching {
            api.sendProblemResults(problemSubmissionRequests, auth)
        }.onSuccess {
            _problemSubmit.value = ProblemSubmitState.Success
            onDone(true)
        }.onFailure { e ->
            onDone(false)
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _problemSubmit.value = it },
                unauthorizedState = ProblemSubmitState.SessionExpired,
                notFoundState = ProblemSubmitState.NotFound,
                failedState = ProblemSubmitState.Failed
            )
        }
    }
    fun resetSubmit() { _submit.value = SubmitState.Idle }
    fun resetProblemSubmit() { _problemSubmit.value = ProblemSubmitState.Idle }

    private val _bookmark = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val bookmark = _bookmark.asStateFlow()

    fun toggleBookmark(problemId: Int) = viewModelScope.launch {
        val current = _bookmark.value[problemId] ?: false
        val newValue = !current

        _bookmark.value = _bookmark.value.toMutableMap().apply {
            put(problemId, newValue)
        }

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)

            _bookmark.value = _bookmark.value.toMutableMap().apply {
                put(problemId, current)
            }
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        val request = BookmarksRequest(problemId)

        runCatching {
            if (newValue) {
                api.addBookmark(auth, request)
            } else {
                api.removeBookmark(auth, request)
            }
        }.onFailure { e ->
            Log.e("Bookmark", "removeBookmark failed", e)
            _bookmark.value = _bookmark.value.toMutableMap().apply {
                put(problemId, current)
            }
        }
    }
    fun initBookmarks(problems: List<Problems>) {
        _bookmark.value = problems.associate { p ->
            p.problemId to (p.isBookmarked ?: false)
        }
    }

    fun removeWrongAnswered(
        problemId: Int,
        onDone: (Boolean) -> Unit = {}
    ) = viewModelScope.launch {
        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _problemSubmit.value = ProblemSubmitState.SessionExpired
            onDone(false)
            return@launch
        }

        val auth = "Bearer ${session.accessToken}"
        val request = BookmarksRequest(problemId)

        runCatching {
            api.removeWrongAnswered(auth, request)
        }.onSuccess {
            _problemSubmit.value = ProblemSubmitState.Success
            onDone(true)
        }.onFailure { e ->
            onDone(false)
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _problemSubmit.value = it },
                unauthorizedState = ProblemSubmitState.SessionExpired,
                notFoundState = ProblemSubmitState.NotFound,
                failedState = ProblemSubmitState.Failed
            )
        }
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