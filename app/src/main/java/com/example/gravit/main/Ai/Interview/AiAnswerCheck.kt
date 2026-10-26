package com.inuappcenter.gravit.main.Ai.Interview

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.inuappcenter.gravit.main.User.TopBar
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun AiAnswerCheckPage(
    navController: NavController,
    mode: AnswerMode,
    vm: AiInterviewVM
) {
    StatusBarStyle(darkIcons = true)

    val scrollState = rememberScrollState()
    var expandedAnswerNumbers by remember { mutableStateOf(setOf(1)) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var playingAnswerNumber by remember { mutableStateOf<Int?>(null) }
    var pausedByExitDialog by remember { mutableStateOf(false) }

    val startState by vm.startState.collectAsState()
    val submitState by vm.submitState.collectAsState()
    val abandonState by vm.abandonState.collectAsState()
    val transcriptionState by vm.transcriptionState.collectAsState()

    var progressStep by rememberSaveable { mutableIntStateOf(0) }
    val progress = remember(progressStep) { Animatable(progressStep / 5f) }
    var minLoadingPassed by remember { mutableStateOf(false) }
    /*
    var transcriptionDisplayedStep by remember { mutableIntStateOf(1) }
    var transcriptionFirstStepPassed by remember { mutableStateOf(false) }
    var transcriptionLoadingFinished by remember { mutableStateOf(false) }
    */

    val interview = startState as? AiInterviewVM.StartUiState.Success ?: return
    val questions = interview.questions
    if (questions.isEmpty()) {
        return
    }
    var answers by remember { mutableStateOf(questions.map { question ->
        vm.getTextAnswer(question.displayOrder) })
    }
    LaunchedEffect(interview.sessionId, mode) {
        if (mode == AnswerMode.Voice && transcriptionState == AiInterviewVM.TranscriptionUiState.Idle) {
            vm.loadTranscriptions(interview.sessionId)
        }
    }
    BackHandler {
        pausedByExitDialog = true
        showExitDialog = true
    }

    LaunchedEffect(transcriptionState) {
        if (
            transcriptionState is AiInterviewVM.TranscriptionUiState.Success
        ) {
            answers = questions.map { question ->
                vm.getTextAnswer(question.displayOrder)
            }
        }
    }
    /*
    val transcriptionCompletedCount =
        when (val state = transcriptionState) {
            is AiInterviewVM.TranscriptionUiState.Loading -> state.completedCount
            is AiInterviewVM.TranscriptionUiState.Failed -> state.completedCount
            is AiInterviewVM.TranscriptionUiState.Success -> 5
            else -> 0
        }
    LaunchedEffect(mode) {
        if (mode == AnswerMode.Voice) {
            transcriptionDisplayedStep = 1
            transcriptionFirstStepPassed = false
            transcriptionLoadingFinished = false

            delay(2000)

            transcriptionFirstStepPassed = true
        }
    }
    LaunchedEffect(
        transcriptionCompletedCount,
        transcriptionFirstStepPassed
    ) {
        if (
            mode == AnswerMode.Voice &&
            transcriptionFirstStepPassed &&
            transcriptionCompletedCount > transcriptionDisplayedStep
        ) {
            transcriptionDisplayedStep = transcriptionCompletedCount
        }
    }
    LaunchedEffect(
        transcriptionDisplayedStep,
        transcriptionState
    ) {
        if (
            mode == AnswerMode.Voice &&
            transcriptionDisplayedStep == 5 &&
            transcriptionState is AiInterviewVM.TranscriptionUiState.Success
        ) {
            delay(2000)
            transcriptionLoadingFinished = true
        }
    }
    */
    var errorNavigated by remember { mutableStateOf(false) }
    LaunchedEffect(submitState) {
        when (submitState) {
            AiInterviewVM.SubmitUiState.SessionExpired -> {
                if (!errorNavigated) {
                    errorNavigated = true
                    navController.navigate("error/401") {
                        launchSingleTop = true
                    }
                }
            }

            AiInterviewVM.SubmitUiState.NotFound -> {
                if (!errorNavigated) {
                    errorNavigated = true
                    navController.navigate("error/404") {
                        launchSingleTop = true
                    }
                }
            }

            else -> Unit
        }
    }
    LaunchedEffect(transcriptionState) {
        when (transcriptionState) {
            AiInterviewVM.TranscriptionUiState.SessionExpired -> {
                if (!errorNavigated) {
                    errorNavigated = true
                    navController.navigate("error/401") {
                        launchSingleTop = true
                    }
                }
            }

            AiInterviewVM.TranscriptionUiState.NotFound -> {
                if (!errorNavigated) {
                    errorNavigated = true
                    navController.navigate("error/404") {
                        launchSingleTop = true
                    }
                }
            }

            else -> Unit
        }
    }

    var abandonErrorNavigated by remember { mutableStateOf(false) }
    LaunchedEffect(abandonState) {
        when (abandonState) {
            AiInterviewVM.AbandonUiState.Success -> {
                showExitDialog = false
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
    LaunchedEffect(submitState) {
        if (submitState == AiInterviewVM.SubmitUiState.Loading) {
            progress.snapTo(0f)
            progressStep = 0
            minLoadingPassed = false
        }

        if (submitState == AiInterviewVM.SubmitUiState.Success) {
            for (step in (progressStep + 1)..5) {
                progress.animateTo(
                    targetValue = step / 5f,
                    animationSpec = tween(500)
                )
                progressStep = step
                delay(500)
            }
            vm.clearVoiceAnswers()

            navController.navigate("chapter?tab=ai") {
                popUpTo("ai/interview-flow") {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    /*
    if (
        mode == AnswerMode.Voice &&
        !transcriptionLoadingFinished
    ) {
        AiLoading2(
            progress = transcriptionDisplayedStep / 5f,
            step = transcriptionDisplayedStep
        )

        if (
            transcriptionState is AiInterviewVM.TranscriptionUiState.Failed
        ) {
            LoadFailedDialog(
                onRetryClick = { vm.retryTranscriptions(interview.sessionId) }
            )
        }

        return
    }

     */
    if (
        mode == AnswerMode.Voice &&
        (transcriptionState is AiInterviewVM.TranscriptionUiState.Idle ||
                transcriptionState is AiInterviewVM.TranscriptionUiState.Loading ||
                transcriptionState is AiInterviewVM.TranscriptionUiState.Failed
                )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AiLoading1()

            if (transcriptionState is AiInterviewVM.TranscriptionUiState.Failed) {
                LoadFailedDialog(
                    onRetryClick = {
                        vm.retryTranscriptions(interview.sessionId)
                    }
                )
            }

            if (showExitDialog) {
                TwoButtonDialog(
                    title = "정말 나가시겠어요?",
                    description = "지금 나가면 채점 결과가 저장되지 않아요.\n5문항 답변이 모두 사라져요.",
                    leftButtonText = "취소",
                    rightButtonText = "나가기",
                    onDismiss = {
                        if (abandonState != AiInterviewVM.AbandonUiState.Loading) {
                            showExitDialog = false
                            pausedByExitDialog = false
                        }
                    },
                    onRightClick = {
                        if (abandonState != AiInterviewVM.AbandonUiState.Loading) {
                            vm.abandonInterview()
                        }
                    }
                )
            }

            if (abandonState == AiInterviewVM.AbandonUiState.Failed) {
                LoadFailedDialog(
                    onRetryClick = {
                        vm.abandonInterview()
                    }
                )
            }
        }

        return
    }
    if (
        submitState == AiInterviewVM.SubmitUiState.Loading ||
        submitState == AiInterviewVM.SubmitUiState.Success
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AiLoading2(
                progress = progress.value,
                step = progressStep
            )

            if (showExitDialog) {
                TwoButtonDialog(
                    title = "정말 나가시겠어요?",
                    description = "지금 나가면 채점 결과가 저장되지 않아요.\n5문항 답변이 모두 사라져요.",
                    leftButtonText = "취소",
                    rightButtonText = "나가기",
                    onDismiss = {
                        if (abandonState != AiInterviewVM.AbandonUiState.Loading) {
                            showExitDialog = false
                            pausedByExitDialog = false
                        }
                    },
                    onRightClick = {
                        if (abandonState != AiInterviewVM.AbandonUiState.Loading) {
                            vm.abandonInterview()
                        }
                    }
                )
            }

            if (abandonState == AiInterviewVM.AbandonUiState.Failed) {
                LoadFailedDialog(
                    onRetryClick = {
                        vm.abandonInterview()
                    }
                )
            }
            if (submitState == AiInterviewVM.SubmitUiState.Failed) {
                LoadFailedDialog(
                    onRetryClick = {
                        vm.submitAnswers(
                            sessionId = interview.sessionId,
                            mode = mode
                        )
                    }
                )
            }
        }

        return
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.bg0)
                .navigationBarsPadding()
        ) {
            TopBar(
                navController = navController,
                title = "",
                icon = painterResource(id = R.drawable.unselected_home_button),
                height = 48.dp,
                onIconClick = {
                    pausedByExitDialog = true
                    showExitDialog = true
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(20.dp))

                Text(
                    text = "답변을 확인해주세요.",
                    style = AppTypography.Heading2,
                    color = AppColor.text1
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "음성 인식 과정에서 생긴 오타만 고쳐주세요.\n답변 내용을 새로 쓰는 용도는 아니에요.",
                    style = AppTypography.Caption1,
                    color = AppColor.text3
                )

                Spacer(Modifier.height(12.dp))

                questions.forEachIndexed { index, question ->
                    val number = question.displayOrder

                    AiAnswerCheckItem(
                        number = number,
                        question = question.content,
                        answer = answers[index],
                        mode = mode,
                        expanded = number in expandedAnswerNumbers,
                        onClick = {
                            expandedAnswerNumbers =
                                if (number in expandedAnswerNumbers) {
                                    expandedAnswerNumbers - number
                                } else {
                                    expandedAnswerNumbers + number
                                }
                        },
                        onAnswerChange = { changedAnswer ->
                            if (changedAnswer.length <= 2000) {
                                answers = answers.toMutableList().also {
                                    it[index] = changedAnswer
                                }

                                vm.saveTextAnswer(
                                    displayOrder = question.displayOrder,
                                    content = changedAnswer
                                )
                            }
                        },
                        vm = vm,
                        playingAnswerNumber = playingAnswerNumber,
                        onPlayingAnswerChange = { playingAnswerNumber = it },
                        pausedByExitDialog = pausedByExitDialog,
                    )

                    if (index != questions.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Spacer(Modifier.height(40.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 20.dp
                    )
            ) {
                BlockButton(
                    text = if (submitState == AiInterviewVM.SubmitUiState.Loading) {
                        "답변 제출 중"
                    } else {
                        "제출하기"
                    },
                    onClick = {
                        if (submitState != AiInterviewVM.SubmitUiState.Loading) {
                            when (mode) {
                                AnswerMode.Text -> {
                                    vm.submitAnswers(
                                        sessionId = interview.sessionId,
                                        mode = AnswerMode.Text
                                    )
                                }

                                AnswerMode.Voice -> {
                                    vm.submitAnswers(
                                        sessionId = interview.sessionId,
                                        mode = AnswerMode.Voice
                                    )
                                }
                            }
                        }
                    },
                    state = if (submitState == AiInterviewVM.SubmitUiState.Loading) {
                        ButtonState.Secondary
                    } else {
                        ButtonState.Default
                    },
                    style = AppTypography.Headline2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
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
                if (abandonState != AiInterviewVM.AbandonUiState.Loading) {
                    showExitDialog = false
                    pausedByExitDialog = false
                }
            },
            onRightClick = {
                if (abandonState != AiInterviewVM.AbandonUiState.Loading) {
                    vm.abandonInterview()
                }
            }
        )
    }
    if (abandonState == AiInterviewVM.AbandonUiState.Failed) {
        LoadFailedDialog(
            onRetryClick = {
                vm.abandonInterview()
            }
        )
    }
}

@Composable
private fun AiAnswerCheckItem(
    number: Int,
    question: String,
    answer: String,
    mode: AnswerMode,
    expanded: Boolean,
    onClick: () -> Unit,
    onAnswerChange: (String) -> Unit,
    vm: AiInterviewVM,
    playingAnswerNumber: Int?,
    pausedByExitDialog: Boolean,
    onPlayingAnswerChange: (Int?) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppColor.bg0)
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AppColor.bg1)
                    .padding(
                        horizontal = 12.dp,
                        vertical = 2.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "문제 $number",
                    style = AppTypography.Body2_Reading,
                    color = AppColor.text1
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = question,
                style = AppTypography.Body1_Normal,
                color = AppColor.text1
            )
        }

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppColor.bg0)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .clip(RoundedCornerShape(50))
                        .border(
                            width = 1.dp,
                            color = AppColor.bg1,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 2.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "답변 $number",
                        style = AppTypography.Body2_Reading,
                        color = AppColor.Main2
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    text = "펼쳐서 보기",
                    style = AppTypography.Caption1,
                    color = AppColor.text4
                )

                Spacer(Modifier.width(4.dp))

                Icon(
                    painter = painterResource(
                        id = if (expanded) {
                            R.drawable.chevron_up
                        } else {
                            R.drawable.chevron_down
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = AppColor.icon_disabled
                )
            }

            AnimatedVisibility(
                visible = expanded
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                ) {
                    AiAnswerEditField(
                        answer = answer,
                        onAnswerChange = onAnswerChange
                    )

                    if (mode == AnswerMode.Voice) {
                        Spacer(Modifier.height(16.dp))

                        AiAnswerAudioPlayer(
                            audioFile = vm.getVoiceAnswer(number),
                            isSelected = playingAnswerNumber == number,
                            pauseForExitDialog = pausedByExitDialog,
                            onPlay = {
                                onPlayingAnswerChange(number)
                            },
                            onStop = {
                                if (playingAnswerNumber == number) {
                                    onPlayingAnswerChange(null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiAnswerEditField(
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 146.dp)
            .clip(RoundedCornerShape(AppRadius.sm))
            .border(1.dp, AppColor.divider1, RoundedCornerShape(AppRadius.sm))
            .background(AppColor.bg1)
            .padding(16.dp)
    ) {
        Text(
            text = "내 답변",
            style = AppTypography.Caption1,
            color = AppColor.text4
        )
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = answer,
            onValueChange = {
                if (it.length <= 2000) {
                    onAnswerChange(it)
                }
            },
            textStyle = AppTypography.Body2_Reading.copy(
                color = AppColor.text1
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun AiAnswerAudioPlayer(
    audioFile: File? = null,
    audioUrl: String? = null,
    isSelected: Boolean,
    pauseForExitDialog: Boolean,
    onPlay: () -> Unit,
    onStop: () -> Unit
) {
    val context = LocalContext.current

    val player = remember(audioFile, audioUrl) {
        ExoPlayer.Builder(context).build()
    }

    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }

    var resumeAfterBackground by remember { mutableStateOf(false) }
    var resumeAfterExitDialog by remember { mutableStateOf(false) }
    val currentOnStop by rememberUpdatedState(onStop)
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    duration = player.duration.coerceAtLeast(0L)
                }

                if (playbackState == Player.STATE_ENDED) {
                    player.pause()
                    player.seekTo(0L)
                    currentPosition = 0L
                    currentOnStop()
                }
            }
        }

        player.addListener(listener)

        val audioUri = when {
            audioFile?.exists() == true -> audioFile.toUri()
            !audioUrl.isNullOrBlank() -> audioUrl.toUri()
            else -> null
        }

        audioUri?.let {
            player.setMediaItem(MediaItem.fromUri(it))
            player.prepare()
        }

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(isSelected) {
        if (!isSelected) {
            player.pause()
            player.seekTo(0L)
            currentPosition = 0L
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = player.currentPosition
            duration = player.duration.coerceAtLeast(0L)
            delay(100)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, player) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (player.isPlaying) {
                        resumeAfterBackground = true
                        player.pause()
                    }
                }
                Lifecycle.Event.ON_START -> {
                    if (
                        resumeAfterBackground &&
                        isSelected &&
                        !pauseForExitDialog
                    ) {
                        player.play()
                        resumeAfterBackground = false
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(pauseForExitDialog) {
        if (pauseForExitDialog) {
            if (isSelected && player.isPlaying) {
                resumeAfterExitDialog = true
                player.pause()
            }
        } else {
            if (
                resumeAfterExitDialog &&
                isSelected
            ) {
                player.play()
                resumeAfterExitDialog = false
            }
        }
    }
    val progress = if (duration > 0L) {
        currentPosition.toFloat() / duration.toFloat()
    } else {
        0f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .shadow(
                elevation = 1.5.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black.copy(alpha = 0.50f)
            )
            .clip(RoundedCornerShape(50))
            .background(AppColor.bg0)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.play),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    enabled = (audioFile?.exists() == true) || !audioUrl.isNullOrBlank()
                ) {
                    if (isPlaying) {
                        player.pause()
                    } else {
                        onPlay()
                        if (player.playbackState == Player.STATE_IDLE) {
                            player.prepare()
                        }
                        player.play()
                    }
                }
        )

        Spacer(Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(AppColor.bg2)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(AppColor.Main1)
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = formatAudioTime(
                if (duration > 0L) duration else 0L
            ),
            style = AppTypography.Caption1,
            color = AppColor.text1
        )
    }
}

private fun formatAudioTime(
    millis: Long
): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format(
        "%02d:%02d",
        minutes,
        seconds
    )
}