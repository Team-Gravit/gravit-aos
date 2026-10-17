package com.inuappcenter.gravit.main.Ai.Interview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.inuappcenter.gravit.api.ApiService
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.InterviewAnswerRequest
import com.inuappcenter.gravit.api.InterviewAudioUploadRequest
import com.inuappcenter.gravit.api.InterviewAudioUploader
import com.inuappcenter.gravit.api.InterviewQuestion
import com.inuappcenter.gravit.api.InterviewSessionCreateRequest
import com.inuappcenter.gravit.api.InterviewSubmitRequest
import com.inuappcenter.gravit.api.InterviewTranscriptionRequest
import com.inuappcenter.gravit.api.InterviewTranscriptionsResponse
import com.inuappcenter.gravit.error.handleApiFailure
import com.inuappcenter.gravit.main.Ai.Selected.AnswerMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class AiInterviewVM(
    private val api: ApiService,
    private val appContext: Context
) : ViewModel() {

    sealed interface StartUiState {
        data object Idle : StartUiState
        data object Loading : StartUiState
        data class Success(
            val sessionId: Long,
            val questions: List<InterviewQuestion>
        ) : StartUiState
        data object Failed : StartUiState
        data object SessionExpired : StartUiState
        data object NotFound : StartUiState
    }
    sealed interface SubmitUiState {
        data object Idle : SubmitUiState
        data object Loading : SubmitUiState
        data object Success : SubmitUiState
        data object Failed : SubmitUiState
        data object SessionExpired : SubmitUiState
        data object NotFound : SubmitUiState
    }

    sealed interface VoiceProcessUiState {
        data object Idle : VoiceProcessUiState
        data class Loading(val displayOrder: Int) : VoiceProcessUiState
        data class Success(val displayOrder: Int) : VoiceProcessUiState
        data object Failed : VoiceProcessUiState
        data object SessionExpired : VoiceProcessUiState
        data object NotFound : VoiceProcessUiState
    }

    sealed interface TranscriptionUiState {
        data object Idle : TranscriptionUiState
        data class Loading(val completedCount: Int) : TranscriptionUiState
        data class Success(val data: InterviewTranscriptionsResponse) : TranscriptionUiState
        data class Failed(val completedCount: Int) : TranscriptionUiState
        data object SessionExpired : TranscriptionUiState
        data object NotFound : TranscriptionUiState
    }


    private val _startState = MutableStateFlow<StartUiState>(StartUiState.Idle)
    val startState = _startState.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitUiState>(SubmitUiState.Idle)
    val submitState = _submitState.asStateFlow()

    private var createdSessionId: Long? = null

    private val _voiceProcessState = MutableStateFlow<VoiceProcessUiState>(VoiceProcessUiState.Idle)
    val voiceProcessState = _voiceProcessState.asStateFlow()

    private val _transcriptionState = MutableStateFlow<TranscriptionUiState>(TranscriptionUiState.Idle)
    val transcriptionState = _transcriptionState.asStateFlow()
    private var transcriptionRetryOrders = emptyList<Int>()
    private var transcriptionRetryGetOnly = false

    private val transcriptionRequested = mutableSetOf<Int>()

    fun startInterview(
        inputType: String,
        mode: String,
        difficulty: String,
        stack: String?,
        topics: List<String>
    ) = viewModelScope.launch {
        if (_startState.value == StartUiState.Loading) {
            return@launch
        }

        _startState.value = StartUiState.Loading

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _startState.value = StartUiState.SessionExpired
            return@launch
        }

        val request = InterviewSessionCreateRequest(
            inputType = inputType,
            mode = mode,
            difficulty = difficulty,
            stack = stack,
            topics = topics
        )

        runCatching {
            val sessionId = createdSessionId
                ?: api.createInterviewSession(
                    auth = "Bearer ${session.accessToken}",
                    request = request
                ).sessionId.also {
                    createdSessionId = it
                }

            val questionsResponse =
                api.getInterviewSessionQuestions(
                    auth = "Bearer ${session.accessToken}",
                    sessionId = sessionId
                )

            StartUiState.Success(
                sessionId = sessionId,
                questions = questionsResponse.questions
            )
        }.onSuccess {
            _startState.value = it
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _startState.value = it },
                unauthorizedState = StartUiState.SessionExpired,
                notFoundState = StartUiState.NotFound,
                failedState = StartUiState.Failed
            )
        }
    }
    fun submitAnswers(
        sessionId: Long,
        mode: AnswerMode
    ) = viewModelScope.launch {
        if (_submitState.value == SubmitUiState.Loading) {
            return@launch
        }

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _submitState.value = SubmitUiState.SessionExpired
            return@launch
        }

        if (
            mode == AnswerMode.Voice &&
            (1..5).any { voiceAudioKeys[it] == null }
        ) {
            _submitState.value = SubmitUiState.Failed
            return@launch
        }

        val request = InterviewSubmitRequest(
            answers = (1..5).map { displayOrder ->
                InterviewAnswerRequest(
                    displayOrder = displayOrder,
                    content = textAnswers[displayOrder],
                    audioKey = when (mode) {
                        AnswerMode.Text -> null
                        AnswerMode.Voice -> voiceAudioKeys[displayOrder]
                    }
                )
            }
        )
        _submitState.value = SubmitUiState.Loading
        runCatching {
            api.submitInterviewAnswers(
                auth = "Bearer ${session.accessToken}",
                sessionId = sessionId,
                request = request
            )
        }.onSuccess {
            _submitState.value = SubmitUiState.Success
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _submitState.value = it },
                unauthorizedState = SubmitUiState.SessionExpired,
                notFoundState = SubmitUiState.NotFound,
                failedState = SubmitUiState.Failed
            )
        }
    }
    //녹음 파일
    private val voiceAnswers = mutableMapOf<Int, File>()
    private val voiceAudioKeys = mutableMapOf<Int, String>()

    fun saveVoiceAnswer(displayOrder: Int, file: File) {
        voiceAnswers[displayOrder] = file
    }

    fun getVoiceAnswer(displayOrder: Int): File? {
        return voiceAnswers[displayOrder]
    }
    fun clearVoiceAnswers() {
        voiceAnswers.values.forEach { file ->
            if (file.exists()) {
                file.delete()
            }
        }

        createdSessionId?.let { sessionId ->
            appContext.cacheDir
                .listFiles()
                ?.filter { file ->
                    file.name.startsWith("interview_${sessionId}_") &&
                            file.extension == "m4a"
                }
                ?.forEach { file ->
                    if (file.exists()) {
                        file.delete()
                    }
                }
        }

        voiceAnswers.clear()
    }
    //텍스트 답변
    private val textAnswers = mutableMapOf<Int, String>()
    fun saveTextAnswer(
        displayOrder: Int,
        content: String
    ) {
        textAnswers[displayOrder] = content
    }
    fun getTextAnswer(
        displayOrder: Int
    ): String {
        return textAnswers[displayOrder].orEmpty()
    }

    fun reset() {
        clearVoiceAnswers()
        createdSessionId = null
        _startState.value = StartUiState.Idle
        _submitState.value = SubmitUiState.Idle
        _abandonState.value = AbandonUiState.Idle
        _voiceProcessState.value = VoiceProcessUiState.Idle
        _transcriptionState.value = TranscriptionUiState.Idle
        voiceAudioKeys.clear()
        transcriptionRequested.clear()
        textAnswers.clear()
        transcriptionRetryOrders = emptyList()
        transcriptionRetryGetOnly = false
    }

    sealed interface AbandonUiState {
        data object Idle : AbandonUiState
        data object Loading : AbandonUiState
        data object Success : AbandonUiState
        data object Failed : AbandonUiState
        data object SessionExpired : AbandonUiState
        data object NotFound : AbandonUiState
    }

    private val _abandonState = MutableStateFlow<AbandonUiState>(AbandonUiState.Idle)
    val abandonState = _abandonState.asStateFlow()
    fun abandonInterview() = viewModelScope.launch {
        if (_abandonState.value == AbandonUiState.Loading) {
            return@launch
        }

        val sessionId = createdSessionId ?: run {
            _abandonState.value = AbandonUiState.Failed
            return@launch
        }

        val session = AuthPrefs.load(appContext)

        if (session == null) {
            AuthPrefs.clear(appContext)
            _abandonState.value = AbandonUiState.SessionExpired
            return@launch
        }

        _abandonState.value = AbandonUiState.Loading

        runCatching {
            api.abandonInterviewSession(
                auth = "Bearer ${session.accessToken}",
                sessionId = sessionId
            )
        }.onSuccess {
            _abandonState.value = AbandonUiState.Success
        }.onFailure { e ->
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _abandonState.value = it },
                unauthorizedState = AbandonUiState.SessionExpired,
                notFoundState = AbandonUiState.NotFound,
                failedState = AbandonUiState.Failed
            )
        }
    }

    fun processVoiceAnswer(
        sessionId: Long,
        displayOrder: Int
    ) = viewModelScope.launch {
        if (_voiceProcessState.value is VoiceProcessUiState.Loading) {
            return@launch
        }

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _voiceProcessState.value = VoiceProcessUiState.SessionExpired
            return@launch
        }

        val file = voiceAnswers[displayOrder]
        if (file == null || !file.exists()) {
            _voiceProcessState.value = VoiceProcessUiState.Failed
            return@launch
        }

        _voiceProcessState.value = VoiceProcessUiState.Loading(displayOrder)

        try {
            var audioKey = voiceAudioKeys[displayOrder]

            if (audioKey == null) {
                val uploadInfo = api.createInterviewAudioUpload(
                    auth = "Bearer ${session.accessToken}",
                    sessionId = sessionId,
                    request = InterviewAudioUploadRequest(
                        displayOrder = displayOrder,
                        contentType = "audio/m4a"
                    )
                )

                val body = file.asRequestBody("audio/m4a".toMediaType())

                val uploadResponse = InterviewAudioUploader.api.uploadAudio(
                    uploadUrl = uploadInfo.uploadUrl,
                    contentType = "audio/m4a",
                    body = body
                )

                if (!uploadResponse.isSuccessful) {
                    _voiceProcessState.value = VoiceProcessUiState.Failed
                    return@launch
                }

                audioKey = uploadInfo.audioKey
                voiceAudioKeys[displayOrder] = audioKey
            }

            if (displayOrder !in transcriptionRequested) {
                val response = api.requestTranscription(
                    auth = "Bearer ${session.accessToken}",
                    sessionId = sessionId,
                    request = InterviewTranscriptionRequest(
                        displayOrder = displayOrder,
                        audioKey = audioKey
                    )
                )

                if (!response.isSuccessful) {
                    throw HttpException(response)
                }

                transcriptionRequested += displayOrder
            }

            _voiceProcessState.value =
                VoiceProcessUiState.Success(displayOrder)

        } catch (e: Exception) {
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _voiceProcessState.value = it },
                unauthorizedState = VoiceProcessUiState.SessionExpired,
                notFoundState = VoiceProcessUiState.NotFound,
                failedState = VoiceProcessUiState.Failed
            )
        }
    }
    fun loadTranscriptions(sessionId: Long) = viewModelScope.launch {
        if (_transcriptionState.value is TranscriptionUiState.Loading) {
            return@launch
        }

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _transcriptionState.value = TranscriptionUiState.SessionExpired
            return@launch
        }

        val previousCompletedCount =
            when (val state = _transcriptionState.value) {
                is TranscriptionUiState.Loading -> state.completedCount
                is TranscriptionUiState.Failed -> state.completedCount
                else -> 0
            }

        _transcriptionState.value = TranscriptionUiState.Loading(previousCompletedCount)

        try {
            while (true) {
                val response = api.getTranscriptions(
                    auth = "Bearer ${session.accessToken}",
                    sessionId = sessionId
                )

                val completedCount = response.transcriptions.count {
                    it.status == "COMPLETED"
                }

                _transcriptionState.value = TranscriptionUiState.Loading(completedCount)

                val retryOrders = response.transcriptions
                    .filter {
                        it.status == "FAILED" || it.status == null
                    }
                    .map {
                        it.displayOrder
                    }

                if (retryOrders.isNotEmpty()) {
                    transcriptionRetryOrders = retryOrders
                    transcriptionRetryGetOnly = false

                    _transcriptionState.value = TranscriptionUiState.Failed(completedCount = completedCount)

                    return@launch
                }

                val hasPending = response.transcriptions.any {
                    it.status == "PENDING"
                }

                if (!hasPending) {
                    response.transcriptions.forEach {
                        saveTextAnswer(
                            displayOrder = it.displayOrder,
                            content = it.transcript.orEmpty()
                        )
                    }

                    transcriptionRetryOrders = emptyList()
                    transcriptionRetryGetOnly = false

                    _transcriptionState.value = TranscriptionUiState.Success(response)

                    return@launch
                }

                delay(3000)
            }
        } catch (e: Exception) {
            transcriptionRetryOrders = emptyList()
            transcriptionRetryGetOnly = true

            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _transcriptionState.value = it },
                unauthorizedState = TranscriptionUiState.SessionExpired,
                notFoundState = TranscriptionUiState.NotFound,
                failedState = TranscriptionUiState.Failed(completedCount = previousCompletedCount)
            )
        }
    }
    fun retryTranscriptions(
        sessionId: Long) = viewModelScope.launch {
        if (_transcriptionState.value is TranscriptionUiState.Loading) {
            return@launch
        }

        if (transcriptionRetryGetOnly) {
            _transcriptionState.value = TranscriptionUiState.Idle
            loadTranscriptions(sessionId)
            return@launch
        }

        val session = AuthPrefs.load(appContext)
        if (session == null) {
            AuthPrefs.clear(appContext)
            _transcriptionState.value = TranscriptionUiState.SessionExpired
            return@launch
        }

        val completedCount =
            (_transcriptionState.value as? TranscriptionUiState.Failed)
                ?.completedCount ?: 0

        _transcriptionState.value = TranscriptionUiState.Loading(completedCount)

        try {
            transcriptionRetryOrders.forEach { displayOrder ->
                val audioKey = voiceAudioKeys[displayOrder]

                if (audioKey == null) {
                    _transcriptionState.value = TranscriptionUiState.Failed(completedCount = completedCount)
                    return@launch
                }

                val response = api.requestTranscription(
                    auth = "Bearer ${session.accessToken}",
                    sessionId = sessionId,
                    request = InterviewTranscriptionRequest(
                        displayOrder = displayOrder,
                        audioKey = audioKey
                    )
                )

                if (!response.isSuccessful) {
                    throw HttpException(response)
                }
            }
            transcriptionRetryOrders = emptyList()
            transcriptionRetryGetOnly = false
            _transcriptionState.value = TranscriptionUiState.Idle
            loadTranscriptions(sessionId)
        } catch (e: Exception) {
            handleApiFailure(
                e = e,
                appContext = appContext,
                onStateChange = { _transcriptionState.value = it },
                unauthorizedState = TranscriptionUiState.SessionExpired,
                notFoundState = TranscriptionUiState.NotFound,
                failedState = TranscriptionUiState.Failed(completedCount = completedCount)
            )
        }
    }
    fun resetVoiceProcessState() {
        _voiceProcessState.value = VoiceProcessUiState.Idle
    }

    fun resetTranscriptionState() {
        _transcriptionState.value = TranscriptionUiState.Idle
    }
}

class AiInterviewVMFactory(
    private val api: ApiService,
    context: Context
) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>
    ): T { return AiInterviewVM(api = api, appContext = appContext) as T }
}