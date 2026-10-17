package com.inuappcenter.gravit.main.Ai.Interview

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

private enum class TextInterviewState {
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

    var snackBarText by remember { mutableStateOf<String?>(null) }

    val voiceProcessState by vm.voiceProcessState.collectAsState()
    val startState by vm.startState.collectAsState()
    val abandonState by vm.abandonState.collectAsState()
    var voiceState by rememberSaveable { mutableStateOf(VoiceInterviewState.Ready) }
    var textState by rememberSaveable { mutableStateOf(TextInterviewState.Ready) }
    var autoAnswer by rememberSaveable { mutableStateOf(false) }
    var questionPlaybackEnded by remember { mutableStateOf(false) }
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

    val context = LocalContext.current
    val player = remember { ExoPlayer.Builder(context).build() }
    DisposableEffect(player) {
        val listener =
            object : Player.Listener {
                override fun onPlaybackStateChanged(
                    playbackState: Int
                ) {
                    if (playbackState == Player.STATE_ENDED) {
                        if (autoAnswer) {
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

            if (granted && pendingRecordStart) {
                pendingRecordStart = false
                startRecording()
            } else {
                pendingRecordStart = false
                vm.abandonInterview()
            }
        }

    LaunchedEffect(questionPlaybackEnded, autoAnswer) {
        if (
            questionPlaybackEnded &&
            autoAnswer &&
            voiceState == VoiceInterviewState.PlayingQuestion
        ) {
            delay(3000)

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

    LaunchedEffect(currentQuestion.displayOrder, mode) {
        if (mode != AnswerMode.Voice) {
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
            MediaItem.fromUri(
                audioUrl.toUri()
            )
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
            voiceState ==
            VoiceInterviewState.Recording &&
            !recordingPaused
        ) {
            while (true) {
                delay(1000)
                recordingSeconds++
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
                    question = currentQuestion.content
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
                        state = textState,
                        answerText = answerText,
                        onStartWriting = { textState = TextInterviewState.Writing },
                        onAnswerTextChange = {
                            if (it.length <= 1000) {
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
                horizontalAlignment =
                    Alignment.CenterHorizontally
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
private fun InterviewQuestionContent(
    questionNumber: Int,
    totalQuestionCount: Int,
    question: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 20.dp)
            .clip(RoundedCornerShape(AppRadius.lg))
            .background(AppColor.bg0)
    ) {
        Image(
            painter = painterResource(id = R.drawable.interview_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(horizontalBias = -0.2f, verticalBias = 0f),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
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
                InterviewButton(
                    text = "답변 완료하기   —   ${formatInterviewTime(recordingSeconds)}",
                    onClick = onCompleteAnswer,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    containerColor = AppColor.Main1,
                    textColor = AppColor.CTA_text,
                    borderColor = AppColor.Main1
                )
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
            TextInterviewState.Ready -> {
                InterviewButton(
                    text = "답변 작성하기",
                    onClick = onStartWriting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    containerColor = AppColor.Main1,
                    textColor = AppColor.CTA_text,
                    borderColor = AppColor.Main1
                )
            }

            TextInterviewState.Writing -> {
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