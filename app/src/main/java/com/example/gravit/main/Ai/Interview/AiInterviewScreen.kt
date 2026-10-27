
package com.inuappcenter.gravit.main.Ai.Interview

import android.content.Intent
import android.provider.Settings
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import android.view.TextureView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.gravit.share.LoadFailedDialog
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppRadius
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.main.Ai.Selected.AnswerMode
import com.inuappcenter.gravit.main.Study.Problem.CustomSnackBar
import com.inuappcenter.gravit.main.User.TopBar
import com.inuappcenter.gravit.ui.theme.pretendard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.random.Random
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.material3.HorizontalDivider

enum class InterviewMode {
    Voice,
    Text
}

private enum class VoiceInterviewState {
    PlayingQuestion,
    Ready,
    Recording,
    Processing
}

private enum class InterviewVideoState { Question, Answer, Idle }

private enum class TextInterviewState {
    PlayingQuestion,
    Ready,
    Writing
}

@Composable
fun AiInterviewPage(
    navController: NavController,
    mode: AnswerMode,
    vm: AiInterviewVM
) {
    StatusBarStyle(darkIcons = true)

    val context = LocalContext.current
    var snackBarText by remember { mutableStateOf<String?>(null) }

    val voiceProcessState by vm.voiceProcessState.collectAsState()
    val startState by vm.startState.collectAsState()
    val abandonState by vm.abandonState.collectAsState()
    var voiceState by rememberSaveable { mutableStateOf(VoiceInterviewState.Ready) }
    var textState by rememberSaveable { mutableStateOf(TextInterviewState.Ready) }
    var autoAnswer by rememberSaveable { mutableStateOf(false) }
    var questionPlaybackEnded by remember { mutableStateOf(false) }
    var questionAudioPlaying by remember { mutableStateOf(false) }
    var answerText by rememberSaveable { mutableStateOf("") }
    var recordingSeconds by rememberSaveable { mutableIntStateOf(0) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val interview =
        startState as? AiInterviewVM.StartUiState.Success
            ?: return

    val sessionId = interview.sessionId
    val questions = interview.questions
    if (questions.isEmpty()) {
        return
    }
    var currentQuestionIndex by rememberSaveable { mutableIntStateOf(0) }
    val currentQuestion = questions[currentQuestionIndex]

    val player = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(player) {
        val listener =
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    questionAudioPlaying = isPlaying
                }

                override fun onPlaybackStateChanged(
                    playbackState: Int
                ) {
                    if (playbackState == Player.STATE_ENDED) {
                        questionAudioPlaying = false
                        if (mode == AnswerMode.Voice && autoAnswer) {
                            questionPlaybackEnded = true
                        } else {
                            voiceState = VoiceInterviewState.Ready
                        }
                    }
                }
            }

        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }

    var pendingRecordStart by remember { mutableStateOf(false) }
    var microphonePermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showMicrophonePermissionDialog by remember { mutableStateOf(false) }
    val permissionPrefs = remember(context) {
        context.getSharedPreferences(
            "interview_permission",
            android.content.Context.MODE_PRIVATE
        )
    }

    var hasRequestedMicrophonePermission by remember {
        mutableStateOf(
            permissionPrefs.getBoolean("microphone_requested", false)
        )
    }
    var pausedByBackground by remember { mutableStateOf(false) }
    var pausedByExitDialog by remember { mutableStateOf(false) }
    var recordingPaused by remember { mutableStateOf(false) }
    var abandonErrorNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(abandonState) {
        when (abandonState) {
            AiInterviewVM.AbandonUiState.Success -> {
                showExitDialog = false

                mediaRecorder?.apply {
                    try {
                        stop()
                    } catch (_: Exception) {
                    }

                    release()
                }

                mediaRecorder = null

                vm.clearVoiceAnswers()

                navController.navigate("chapter?tab=ai") {
                    popUpTo("ai/interview-flow") {
                        inclusive = true
                    }

                    launchSingleTop = true
                }
            }

            AiInterviewVM.AbandonUiState.Failed -> {
                showExitDialog = false
                snackBarText = "오류가 발생했습니다."
            }

            AiInterviewVM.AbandonUiState.SessionExpired -> {
                if (!abandonErrorNavigated) {
                    abandonErrorNavigated = true

                    navController.navigate("error/401") {
                        launchSingleTop = true
                    }
                }
            }

            AiInterviewVM.AbandonUiState.NotFound -> {
                if (!abandonErrorNavigated) {
                    abandonErrorNavigated = true

                    navController.navigate("error/404") {
                        launchSingleTop = true
                    }
                }
            }

            else -> Unit
        }
    }
    fun completeAnswer() {
        if (voiceState != VoiceInterviewState.Recording) return

        voiceState = VoiceInterviewState.Processing

        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            recordingPaused = false

            val recordedFile = File(
                context.cacheDir,
                "interview_${sessionId}_${currentQuestion.displayOrder}.m4a"
            )

            vm.saveVoiceAnswer(
                displayOrder = currentQuestion.displayOrder,
                file = recordedFile
            )

            vm.processVoiceAnswer(
                sessionId = sessionId,
                displayOrder = currentQuestion.displayOrder
            )
        } catch (e: RuntimeException) {
            mediaRecorder?.release()
            mediaRecorder = null
            recordingPaused = false

            val recordedFile = File(
                context.cacheDir,
                "interview_${sessionId}_${currentQuestion.displayOrder}.m4a"
            )

            if (recordedFile.exists()) {
                recordedFile.delete()
            }

            voiceState = VoiceInterviewState.Ready
            snackBarText = "오류가 발생했습니다."
        }
    }
    fun startRecording() {
        val outputFile = File(
            context.cacheDir,
            "interview_${sessionId}_${currentQuestion.displayOrder}.m4a"
        )

        try {
            val recorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    @Suppress("DEPRECATION")
                    MediaRecorder()
                }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            voiceState = VoiceInterviewState.Recording
            recordingPaused = false
            recordingSeconds = 0
        } catch (e: Exception) {
            mediaRecorder?.release()
            mediaRecorder = null

            if (outputFile.exists()) {
                outputFile.delete()
            }

            voiceState = VoiceInterviewState.Ready
        }
    }

    fun pauseInterview() {
        when (voiceState) {
            VoiceInterviewState.PlayingQuestion -> {
                if (player.isPlaying) {
                    player.pause()
                }
            }

            VoiceInterviewState.Recording -> {
                if (!recordingPaused) {
                    mediaRecorder?.pause()
                    recordingPaused = true
                }
            }

            else -> Unit
        }
    }

    fun resumeInterview() {
        when (voiceState) {
            VoiceInterviewState.PlayingQuestion -> {
                player.play()
            }

            VoiceInterviewState.Recording -> {
                if (recordingPaused) {
                    mediaRecorder?.resume()
                    recordingPaused = false
                }
            }

            else -> Unit
        }
    }

    val microphonePermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            microphonePermissionGranted = granted

            if (granted) {
                showMicrophonePermissionDialog = false

                if (pendingRecordStart) {
                    pendingRecordStart = false
                    startRecording()
                }
            } else {
                pendingRecordStart = false
                showMicrophonePermissionDialog = true
            }
        }
    fun requestMicrophonePermission() {
        hasRequestedMicrophonePermission = true

        permissionPrefs.edit()
            .putBoolean("microphone_requested", true)
            .apply()

        microphonePermissionLauncher.launch(
            Manifest.permission.RECORD_AUDIO
        )
    }
    LaunchedEffect(mode) {
        if (mode == AnswerMode.Voice) {
            microphonePermissionGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

            showMicrophonePermissionDialog = !microphonePermissionGranted
        }
    }


    LaunchedEffect(questionPlaybackEnded, autoAnswer) {
        if (
            questionPlaybackEnded &&
            autoAnswer &&
            voiceState == VoiceInterviewState.PlayingQuestion
        ) {
            delay(1000)

            if (
                autoAnswer &&
                voiceState == VoiceInterviewState.PlayingQuestion
            ) {
                if (
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startRecording()
                } else {
                    pendingRecordStart = true
                    microphonePermissionLauncher.launch(
                        Manifest.permission.RECORD_AUDIO
                    )
                }
            }
        } else if (
            questionPlaybackEnded &&
            !autoAnswer &&
            voiceState == VoiceInterviewState.PlayingQuestion
        ) {
            voiceState = VoiceInterviewState.Ready
        }
    }

    LaunchedEffect(currentQuestion.displayOrder, mode, microphonePermissionGranted) {
        if (
            mode == AnswerMode.Voice &&
            !microphonePermissionGranted
        ) {
            return@LaunchedEffect
        }

        val audioUrl = currentQuestion.audioUrl

        if (audioUrl.isNullOrBlank()) {
            voiceState = VoiceInterviewState.Ready
            return@LaunchedEffect
        }

        questionPlaybackEnded = false
        voiceState = VoiceInterviewState.PlayingQuestion

        player.setMediaItem(
            MediaItem.fromUri(audioUrl.toUri())
        )

        player.prepare()
        player.play()
    }

    BackHandler {
        pauseInterview()
        pausedByExitDialog = true
        showExitDialog = true
    }

    LaunchedEffect(voiceState, recordingPaused) {
        if (
            voiceState == VoiceInterviewState.Recording &&
            !recordingPaused
        ) {
            while (recordingSeconds < 120) {
                delay(1000)

                if (recordingSeconds < 120) {
                    recordingSeconds++
                }
            }

            if (recordingSeconds >= 120) {
                completeAnswer()
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _,
                                     event ->

                when (event) {
                    Lifecycle.Event.ON_STOP -> {
                        if (!showExitDialog) {
                            pauseInterview()
                            pausedByBackground = true
                        }
                    }

                    Lifecycle.Event.ON_START -> {
                        if (mode == AnswerMode.Voice) {
                            val granted = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            microphonePermissionGranted = granted
                            showMicrophonePermissionDialog = !granted
                        }

                        if (
                            pausedByBackground &&
                            !showExitDialog
                        ) {
                            resumeInterview()
                            pausedByBackground = false
                        }
                    }

                    else -> Unit
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(voiceProcessState) {
        when (val state = voiceProcessState) {
            is AiInterviewVM
            .VoiceProcessUiState
            .Success -> {

                if (
                    state.displayOrder !=
                    currentQuestion.displayOrder
                ) {
                    return@LaunchedEffect
                }

                vm.resetVoiceProcessState()

                if (
                    currentQuestionIndex <
                    questions.lastIndex
                ) {
                    currentQuestionIndex++
                    recordingSeconds = 0
                    voiceState = VoiceInterviewState.PlayingQuestion

                    scrollState.scrollTo(0)
                } else {
                    navController.navigate("ai/answer-check/${mode.name}") {
                        launchSingleTop = true
                    }
                }
            }
            AiInterviewVM.VoiceProcessUiState.Failed -> {
                snackBarText = "오류가 발생했습니다."
                vm.resetVoiceProcessState()
            }

            AiInterviewVM
                .VoiceProcessUiState
                .SessionExpired -> {

                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            AiInterviewVM
                .VoiceProcessUiState
                .NotFound -> {

                navController.navigate("error/404") {
                    launchSingleTop = true
                }
            }

            else -> Unit
        }
    }
    LaunchedEffect(snackBarText) {
        if (snackBarText != null) {
            delay(2000)
            snackBarText = null
        }
    }

    val videoState = when (mode) {
        AnswerMode.Voice -> when {
            questionAudioPlaying && voiceState == VoiceInterviewState.PlayingQuestion -> InterviewVideoState.Question
            voiceState == VoiceInterviewState.Recording && !recordingPaused -> InterviewVideoState.Answer
            else -> InterviewVideoState.Idle
        }
        AnswerMode.Text -> when {
            questionAudioPlaying -> InterviewVideoState.Question
            textState == TextInterviewState.Writing -> InterviewVideoState.Answer
            else -> InterviewVideoState.Idle
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg0)
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg0)
            ) {
                TopBar(
                    navController = navController,
                    title = "",
                    icon = painterResource(id = R.drawable.unselected_home_button),
                    height = 48.dp,
                    onIconClick = {
                        pauseInterview()
                        pausedByExitDialog = true
                        showExitDialog = true
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .verticalScroll(scrollState)
            ) {
                InterviewQuestionContent(
                    questionNumber = currentQuestion.displayOrder,
                    totalQuestionCount = questions.size,
                    question = currentQuestion.content,
                    videoState = videoState,
                    paused = showExitDialog || pausedByBackground
                )
            }

            when (mode) {
                AnswerMode.Voice -> {
                    VoiceInterviewBottomBar(
                        autoAnswer = autoAnswer,
                        state = voiceState,
                        recordingSeconds = recordingSeconds,
                        isProcessing = voiceProcessState is AiInterviewVM.VoiceProcessUiState.Loading,
                        onAutoAnswerChange = { autoAnswer = it },
                        onStartAnswer = {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                startRecording()
                            } else {
                                pendingRecordStart = true
                                microphonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onCompleteAnswer = {
                            completeAnswer()
                        },
                        onRetry = {
                            vm.processVoiceAnswer(
                                sessionId = sessionId,
                                displayOrder = currentQuestion.displayOrder
                            )
                        }
                    )
                }

                AnswerMode.Text -> {
                    TextInterviewBottomBar(
                        state = if (
                            voiceState == VoiceInterviewState.PlayingQuestion
                        ) {
                            TextInterviewState.PlayingQuestion
                        } else {
                            textState
                        },
                        answerText = answerText,
                        onStartWriting = { textState = TextInterviewState.Writing },
                        onAnswerTextChange = {
                            if (it.length <= 2000) {
                                answerText = it
                            }
                        },
                        onSubmit = {
                            vm.saveTextAnswer(
                                displayOrder = currentQuestion.displayOrder,
                                content = answerText
                            )

                            if (
                                currentQuestionIndex <
                                questions.lastIndex
                            ) {
                                currentQuestionIndex++
                                answerText =
                                    vm.getTextAnswer(questions[currentQuestionIndex].displayOrder)
                                textState = TextInterviewState.Ready
                                coroutineScope.launch { scrollState.scrollTo(0) }
                            } else {
                                navController.navigate("ai/answer-check/${mode.name}") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        }

        if (showExitDialog) {
            TwoButtonDialog(
                title = "정말 나가시겠어요?",
                description = "지금 나가면 채점 결과가 저장되지 않아요.\n5문항 답변이 모두 사라져요.",
                leftButtonText = "취소",
                rightButtonText = "나가기",
                onDismiss = {
                    if (
                        abandonState !=
                        AiInterviewVM
                            .AbandonUiState
                            .Loading
                    ) {
                        showExitDialog = false

                        if (pausedByExitDialog) {
                            resumeInterview()
                            pausedByExitDialog = false
                        }
                    }
                },
                onRightClick = {
                    if (
                        abandonState !=
                        AiInterviewVM
                            .AbandonUiState
                            .Loading
                    ) {
                        vm.abandonInterview()
                    }
                }
            )
        }

        if (abandonState == AiInterviewVM.AbandonUiState.Failed) {
            LoadFailedDialog(onRetryClick = { vm.abandonInterview() })
        }
        if (voiceProcessState is AiInterviewVM.VoiceProcessUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = AppColor.Main1,
                    strokeWidth = 3.dp
                )
            }
        }
        if (snackBarText != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                CustomSnackBar(text = snackBarText!!)
            }
        }
        if (showMicrophonePermissionDialog && mode == AnswerMode.Voice) {
            TwoButtonDialog(
                title = "마이크 권한이 필요해요",
                description = "음성 면접을 진행하려면 마이크 권한을 허용해주세요.",
                leftButtonText = "홈으로 가기",
                rightButtonText = "확인하기",
                onDismiss = {
                    showMicrophonePermissionDialog = false
                    vm.abandonInterview()
                },
                onRightClick = {
                    if (hasRequestedMicrophonePermission) {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )

                        context.startActivity(intent)
                    } else {
                        requestMicrophonePermission()
                    }
                }
            )
        }
    }
}

@Composable
fun TwoButtonDialog(
    title: String,
    description: String,
    leftButtonText: String,
    rightButtonText: String,
    onDismiss: () -> Unit,
    onRightClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .width(328.dp)
                .padding(bottom = 16.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .width(328.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = AppTypography.Headline1,
                    textAlign = TextAlign.Center,
                    color = AppColor.text1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = AppTypography.Label1,
                    color = AppColor.text4,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    BlockButton(
                        state = ButtonState.Stroke,
                        onClick = { onDismiss() },
                        text = leftButtonText,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BlockButton(
                        state = ButtonState.Default,
                        onClick = { onRightClick() },
                        text = rightButtonText,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InterviewVideoPlayer(
    state: InterviewVideoState,
    paused: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTablet = LocalConfiguration.current.screenWidthDp >= 600

    val questionRes = if (isTablet) {
        R.raw.interview_question_web
    } else {
        R.raw.interview_question
    }

    val answers = remember(isTablet) {
        if (isTablet) {
            intArrayOf(
                R.raw.interview_answer_1_web,
                R.raw.interview_answer_2_web,
                R.raw.interview_answer_3_web
            )
        } else {
            intArrayOf(
                R.raw.interview_answer_1,
                R.raw.interview_answer_2,
                R.raw.interview_answer_3
            )
        }
    }

    val players = remember(context) {
        List(2) {
            ExoPlayer.Builder(context).build().apply {
                volume = 0f
                playWhenReady = false
            }
        }
    }

    var visibleIndex by remember {mutableIntStateOf(-1) }
    var loadedResource by remember { mutableIntStateOf(-1) }
    var pendingIndex by remember { mutableIntStateOf(-1) }
    var pendingResource by remember { mutableIntStateOf(-1) }
    var answerResource by remember(isTablet) { mutableIntStateOf(answers[0]) }
    var lastAnswer by remember { mutableIntStateOf(-1) }
    var attachedViews by remember { mutableIntStateOf(0) }

    val currentState by rememberUpdatedState(state)
    val currentPaused by rememberUpdatedState(paused)
    val currentAnswers by rememberUpdatedState(answers)

    fun selectAnswer(): Int {
        val choices = if (lastAnswer == 2) {
            intArrayOf(0, 0, 0, 0, 0, 0, 1, 1, 1)
        } else {
            intArrayOf(0, 0, 0, 0, 0, 0, 1, 1, 1, 2)
        }

        val selected = choices[Random.nextInt(choices.size)]
        lastAnswer = selected

        return currentAnswers[selected]
    }
    LaunchedEffect(state) {
        if (state == InterviewVideoState.Answer) {
            answerResource = selectAnswer()
        }
    }
    fun shouldPlay(): Boolean {
        return currentState != InterviewVideoState.Idle &&
                !currentPaused
    }

    fun prepareVideo(index: Int, resId: Int) {
        val target = players[index]

        pendingIndex = index
        pendingResource = resId

        target.pause()

        target.repeatMode = Player.REPEAT_MODE_ONE

        target.setMediaItem(
            MediaItem.fromUri(
                "android.resource://${context.packageName}/$resId"
            )
        )
        target.prepare()
        target.play()
    }

    DisposableEffect(players) {
        val listeners = players.mapIndexed { index, videoPlayer ->

            object : Player.Listener {

                override fun onRenderedFirstFrame() {
                    if (pendingIndex != index) return

                    loadedResource = pendingResource

                    pendingIndex = -1
                    pendingResource = -1

                    visibleIndex = index

                    if (shouldPlay()) {
                        videoPlayer.play()
                    } else {
                        videoPlayer.pause()
                    }
                }

                override fun onPlayerError(
                    error: PlaybackException
                ) {
                    android.util.Log.e(
                        "InterviewVideo",
                        "Video playback failed",
                        error
                    )
                }

            }.also {
                videoPlayer.addListener(it)
            }
        }

        onDispose {
            players.forEachIndexed { index, videoPlayer ->
                videoPlayer.removeListener(listeners[index])
                videoPlayer.release()
            }
        }
    }

    val targetResource = when (state) {
        InterviewVideoState.Question -> questionRes

        InterviewVideoState.Answer,
        InterviewVideoState.Idle -> answerResource
    }

    LaunchedEffect(
        targetResource,
        state,
        paused,
        attachedViews
    ) {
        if (attachedViews < 2) return@LaunchedEffect

        when {
            targetResource == loadedResource -> {
                if (shouldPlay()) {
                    players[visibleIndex].play()
                } else {
                    players[visibleIndex].pause()
                }
            }
            targetResource == pendingResource -> Unit
            else -> {
                val nextIndex = if (visibleIndex == -1) {
                    0
                } else {
                    1 - visibleIndex
                }

                prepareVideo(
                    index = nextIndex,
                    resId = targetResource
                )
            }
        }
    }

    val alpha0 by animateFloatAsState(
        targetValue = if (visibleIndex == 0) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "videoAlpha0"
    )

    val alpha1 by animateFloatAsState(
        targetValue = if (visibleIndex == 1) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "videoAlpha1"
    )

    LaunchedEffect(visibleIndex) {
        if (visibleIndex == -1) return@LaunchedEffect

        delay(520)

        val previousIndex = 1 - visibleIndex

        if (pendingIndex != previousIndex) {
            players[previousIndex].pause()
        }
    }
    fun applyCrop(view: TextureView, videoWidth: Int, videoHeight: Int) {
        if (view.width == 0 || view.height == 0 ||
            videoWidth == 0 || videoHeight == 0) return

        val viewW = view.width.toFloat()
        val viewH = view.height.toFloat()

        val scale = maxOf(
            viewW / videoWidth,
            viewH / videoHeight
        )

        val matrix = Matrix().apply {
            setScale(
                videoWidth * scale / viewW,
                videoHeight * scale / viewH,
                viewW / 2f,
                viewH / 2f
            )
        }

        view.setTransform(matrix)
    }
    Box(modifier = modifier) {

        players.forEachIndexed { index, videoPlayer ->

            AndroidView(
                factory = { ctx ->
                    TextureView(ctx).also { textureView ->
                        videoPlayer.setVideoTextureView(textureView)
                        attachedViews++

                        textureView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                            val size = videoPlayer.videoSize
                            applyCrop(textureView, size.width, size.height)
                        }
                    }
                },
                update = { textureView ->
                    val size = videoPlayer.videoSize
                    applyCrop(textureView, size.width, size.height)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = if (index == 0) alpha0 else alpha1
                    },
                onRelease = { textureView ->
                    videoPlayer.clearVideoTextureView(textureView)
                    attachedViews--
                }
            )
        }
    }
}

@Composable
private fun InterviewQuestionContent(
    questionNumber: Int,
    totalQuestionCount: Int,
    question: String,
    videoState: InterviewVideoState,
    paused: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp)
            .clip(RoundedCornerShape(AppRadius.lg))
            .background(AppColor.bg0)
    ) {
        val isTablet = LocalConfiguration.current.screenWidthDp >= 600

        InterviewVideoPlayer(
            state = videoState,
            paused = paused,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    if (isTablet) 21f / 9f
                    else 3f / 4f
                )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "$questionNumber/$totalQuestionCount",
                color = AppColor.text4,
                style = AppTypography.Label1
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = question,
                color = AppColor.text1,
                fontFamily = pretendard,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun VoiceInterviewBottomBar(
    autoAnswer: Boolean,
    state: VoiceInterviewState,
    recordingSeconds: Int,
    isProcessing: Boolean,
    onAutoAnswerChange: (Boolean) -> Unit,
    onStartAnswer: () -> Unit,
    onCompleteAnswer: () -> Unit,
    onRetry: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColor.bg0)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Switch(
                checked = autoAnswer,
                onCheckedChange = onAutoAnswerChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AppColor.bg0,
                    uncheckedThumbColor = AppColor.bg0,
                    checkedTrackColor = AppColor.Main1,
                    uncheckedTrackColor = AppColor.divider1,
                    uncheckedBorderColor = Color.Transparent,
                    checkedBorderColor = Color.Transparent
                )
            )

            Text(
                text = "자동 답변",
                color = AppColor.text3,
                style = AppTypography.Caption1
            )
        }

        Spacer(Modifier.width(12.dp))

        when (state) {
            VoiceInterviewState.PlayingQuestion -> {
                InterviewButton(
                    text = "질문 재생 중",
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    containerColor = AppColor.bg0,
                    textColor = AppColor.text4,
                    borderColor = AppColor.divider1
                )
            }

            VoiceInterviewState.Ready -> {
                InterviewButton(
                    text = "답변 시작하기",
                    onClick = onStartAnswer,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    containerColor = AppColor.Main1,
                    textColor = AppColor.CTA_text,
                    borderColor = AppColor.Main1
                )
            }

            VoiceInterviewState.Recording -> {
                val remainingSeconds = (120 - recordingSeconds).coerceAtLeast(0)
                val progress = (recordingSeconds / 120f).coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background(AppColor.Main1)
                        .clickable(onClick = onCompleteAnswer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(Color(0xFFE8C5FF))
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "답변 완료하기",
                            color = AppColor.CTA_text,
                            style = AppTypography.Headline2
                        )

                        Spacer(Modifier.width(12.dp))

                        HorizontalDivider(modifier = Modifier.width(23.dp), 2.dp, AppColor.bg0)

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = formatInterviewTime(remainingSeconds),
                            color = AppColor.CTA_text,
                            style = AppTypography.Headline2
                        )
                    }
                }
            }

            VoiceInterviewState.Processing -> {
                InterviewButton(
                    text =
                        if (isProcessing) {
                            "답변 처리 중"
                        } else {
                            "다시 시도하기"
                        },
                    onClick = onRetry,
                    enabled = !isProcessing,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    containerColor = AppColor.Main1,
                    textColor = AppColor.CTA_text,
                    borderColor = AppColor.Main1
                )
            }
        }
    }
}

@Composable
fun InterviewButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color,
    containerColor: Color,
    borderColor: Color
) {
    Button(
        modifier = modifier
            .border(1.dp, borderColor, RoundedCornerShape(AppRadius.max)),
        onClick = onClick,
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor
            )
    ) {
        Text(
            text = text,
            style = AppTypography.Headline2,
            color = textColor
        )
    }
}

@Composable
private fun TextInterviewBottomBar(
    state: TextInterviewState,
    answerText: String,
    onStartWriting: () -> Unit,
    onAnswerTextChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColor.bg0)
            .padding(16.dp)
    ) {
        when (state) {
            TextInterviewState.PlayingQuestion -> {
                InterviewButton(
                    text = "질문 재생 중",
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    containerColor = AppColor.bg0,
                    textColor = AppColor.text4,
                    borderColor = AppColor.divider1
                )
            }

            TextInterviewState.Ready -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextField(
                        value = answerText,
                        onValueChange = onAnswerTextChange,
                        textStyle = AppTypography.Body2_Normal,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        placeholder = {
                            Text(
                                text = "답변을 입력해주세요.",
                                color = AppColor.text3,
                                style = AppTypography.Body1_Normal
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = AppColor.bg1,
                            unfocusedContainerColor = AppColor.bg1,
                            disabledContainerColor = AppColor.bg1,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = AppColor.text1,
                            unfocusedTextColor = AppColor.text1
                        )
                    )

                    Spacer(Modifier.width(16.dp))

                    Image(
                        painter = painterResource(id = R.drawable.text_send_icon),
                        contentDescription = "답변 제출",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable(
                                enabled = answerText.isNotBlank(),
                                onClick = onSubmit
                            )
                    )
                }
            }

            else -> {}
        }
    }
}

private fun formatInterviewTime(
    seconds: Int
): String {
    val minutes = seconds / 60

    val remainingSeconds = seconds % 60

    return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
}