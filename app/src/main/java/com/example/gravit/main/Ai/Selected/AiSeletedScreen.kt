package com.inuappcenter.gravit.main.Ai.Selected

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.main.Ai.Selected.CsTopicsSkeleton
import com.example.gravit.main.Ai.Selected.StackGroupsSkeleton
import com.example.gravit.main.Ai.Selected.StacksSkeleton
import com.example.gravit.share.LoadFailedDialog
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppRadius
import com.example.gravit.ui.theme.AppSpacing
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Ai.Interview.AiInterviewVM
import com.inuappcenter.gravit.main.Study.Problem.LoadingScreen
import com.inuappcenter.gravit.main.User.TopBar
import kotlinx.coroutines.delay

private enum class AiStep {
    ANSWER_MODE,
    STEP_1,
    STEP_2,
    STEP_3,
    STEP_4,
    STEP_5,
    STEP_6
}

enum class AnswerMode {
    Voice,
    Text
}

@Composable
fun AiSelectedPage(
    navController: NavController,
    interviewVm: AiInterviewVM
) {
    StatusBarStyle(true)
    val context = LocalContext.current
    val vm: AiSelectedVM = viewModel(factory = AiSelectedVMFactory(api = RetrofitInstance.api, context = context))

    val topicsState by vm.topicsState.collectAsState()
    val stackGroupsState by vm.stackGroupsState.collectAsState()
    val stacksState by vm.stacksState.collectAsState()
    val startState by interviewVm.startState.collectAsState()

    var listErrorNavigated by remember { mutableStateOf(false) }
    LaunchedEffect(
        topicsState,
        stackGroupsState,
        stacksState
    ) {
        if (listErrorNavigated) {
            return@LaunchedEffect
        }

        val sessionExpired =
            topicsState is AiSelectedVM.TopicsUiState.SessionExpired ||
                    stackGroupsState is AiSelectedVM.StackGroupsUiState.SessionExpired ||
                    stacksState is AiSelectedVM.StacksUiState.SessionExpired

        val notFound =
            topicsState is AiSelectedVM.TopicsUiState.NotFound ||
                    stackGroupsState is AiSelectedVM.StackGroupsUiState.NotFound ||
                    stacksState is AiSelectedVM.StacksUiState.NotFound

        when {
            sessionExpired -> {
                listErrorNavigated = true
                vm.resetStates()

                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            notFound -> {
                listErrorNavigated = true
                vm.resetStates()

                navController.navigate("error/404") {
                    launchSingleTop = true
                }
            }
        }
    }

    var step by rememberSaveable { mutableStateOf(AiStep.ANSWER_MODE) }

    var answerMode by rememberSaveable { mutableStateOf<AnswerMode?>(null) }
    var selectedTopic by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedCsTopics by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedCsTopicNames by rememberSaveable { mutableStateOf(setOf<String>()) }
    var selectedJob by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedJobName by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedStack by rememberSaveable {mutableStateOf<String?>(null) }
    var selectedStackName by rememberSaveable { mutableStateOf<String?>(null) }

    var selectedLevel by rememberSaveable { mutableStateOf<String?>(null) }
    var startAttempt by rememberSaveable { mutableIntStateOf(0) }
    var minLoadingPassed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadTopics()
        vm.loadStackGroups()
    }

    LaunchedEffect(selectedJob) {
        selectedJob?.let {
            vm.loadStacks(it)
        }
    }

    LaunchedEffect(startAttempt) {
        if (startAttempt > 0) {
            minLoadingPassed = false
            delay(2000)
            minLoadingPassed = true
        }
    }

    LaunchedEffect(startState, minLoadingPassed) {
        if (startAttempt == 0 || !minLoadingPassed) {
            return@LaunchedEffect
        }

        when (startState) {
            is AiInterviewVM.StartUiState.Success -> {
                navController.navigate(
                    "ai/interview/${answerMode?.name ?: AnswerMode.Voice.name}"
                ) {
                    launchSingleTop = true
                }
            }

            AiInterviewVM.StartUiState.SessionExpired -> {
                interviewVm.reset()
                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            AiInterviewVM.StartUiState.NotFound -> {
                interviewVm.reset()
                navController.navigate("error/404") {
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }

    val nextEnabled = when (step) {
        AiStep.ANSWER_MODE -> answerMode != null
        AiStep.STEP_1 -> selectedTopic != null
        AiStep.STEP_2 -> selectedCsTopics.isNotEmpty()
        AiStep.STEP_3 -> selectedJob != null
        AiStep.STEP_4 -> selectedStack != null
        AiStep.STEP_5 -> selectedLevel != null
        AiStep.STEP_6 -> true
    }

    val listLoadFailed = when (step) {
        AiStep.STEP_2 -> topicsState is AiSelectedVM.TopicsUiState.Failed
        AiStep.STEP_3 -> stackGroupsState is AiSelectedVM.StackGroupsUiState.Failed
        AiStep.STEP_4 -> stacksState is AiSelectedVM.StacksUiState.Failed
        else -> false
    }
    fun retryListLoad() {
        when (step) {
            AiStep.STEP_2 -> { vm.loadTopics() }
            AiStep.STEP_3 -> { vm.loadStackGroups() }
            AiStep.STEP_4 -> {
                selectedJob?.let {
                    vm.loadStacks(it)
                }
            }
            else -> Unit
        }
    }

    fun startInterview() {
        if (startState is AiInterviewVM.StartUiState.Loading) {
            return
        }

        startAttempt++
        minLoadingPassed = false

        interviewVm.startInterview(
            inputType = if (answerMode == AnswerMode.Voice) {
                "VOICE"
            } else {
                "TEXT"
            },
            mode = if (selectedTopic == "CS") {
                "COMMON_CS"
            } else {
                "JOB_SPECIFIC"
            },
            difficulty = when (selectedLevel) {
                "상" -> "HARD"
                "중" -> "NORMAL"
                "하" -> "EASY"
                else -> "NORMAL"
            },
            stack = if (selectedTopic == "JOB") {
                selectedStack
            } else {
                null
            },
            topics = if (selectedTopic == "CS") {
                selectedCsTopics.toList()
            } else {
                emptyList()
            }
        )
    }

    val showFailed = startAttempt > 0 && minLoadingPassed && startState is AiInterviewVM.StartUiState.Failed
    val showLoading = startAttempt > 0 && !showFailed

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            showFailed -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoadingScreen()
                    LoadFailedDialog(onRetryClick = { startInterview() })
                }
            }
            showLoading -> {
                LoadingScreen()
            }
            else -> {
                AiSelectedContent(
                    navController = navController,
                    step = step,
                    answerMode = answerMode,
                    selectedTopic = selectedTopic,
                    selectedCsTopics = selectedCsTopics,
                    selectedCsTopicNames = selectedCsTopicNames,
                    selectedJob = selectedJob,
                    selectedJobName = selectedJobName,
                    selectedStack = selectedStack,
                    selectedStackName = selectedStackName,
                    selectedLevel = selectedLevel,
                    topicsState = topicsState,
                    stackGroupsState = stackGroupsState,
                    stacksState = stacksState,
                    nextEnabled = nextEnabled,
                    onAnswerModeSelected = { answerMode = it },
                    onTopicSelected = { selectedTopic = it },
                    onCsTopicsChanged = { values, names ->
                        selectedCsTopics = values
                        selectedCsTopicNames = names
                    },
                    onJobSelected = { job, jobName ->
                        if (selectedJob != job) {
                            selectedJob = job
                            selectedJobName = jobName
                            selectedStack = null
                            selectedStackName = null
                        }
                    },
                    onStackSelected = { value, name ->
                        selectedStack = value
                        selectedStackName = name
                    },
                    onLevelSelected = { selectedLevel = it },
                    onNext = {
                        step = when (step) {
                            AiStep.ANSWER_MODE -> AiStep.STEP_1
                            AiStep.STEP_1 -> {
                                if (selectedTopic == "CS") {
                                    AiStep.STEP_2
                                } else {
                                    AiStep.STEP_3
                                }
                            }
                            AiStep.STEP_2 -> AiStep.STEP_5
                            AiStep.STEP_3 -> AiStep.STEP_4
                            AiStep.STEP_4 -> AiStep.STEP_5
                            AiStep.STEP_5 -> AiStep.STEP_6
                            AiStep.STEP_6 -> {
                                startInterview()
                                AiStep.STEP_6
                            }
                        }
                    },
                    onPrevious = {
                        step = when (step) {
                            AiStep.ANSWER_MODE -> {
                                navController.popBackStack()
                                AiStep.ANSWER_MODE
                            }
                            AiStep.STEP_1 -> AiStep.ANSWER_MODE
                            AiStep.STEP_2 -> AiStep.STEP_1
                            AiStep.STEP_3 -> AiStep.STEP_1
                            AiStep.STEP_4 -> AiStep.STEP_3
                            AiStep.STEP_5 -> {
                                if (selectedTopic == "CS") {
                                    AiStep.STEP_2
                                } else {
                                    AiStep.STEP_4
                                }
                            }
                            AiStep.STEP_6 -> AiStep.STEP_5
                        }
                    }
                )
            }
        }
        if (listLoadFailed) {
            LoadFailedDialog(
                onRetryClick = {
                    retryListLoad()
                }
            )
        }
    }
}
@Composable
private fun AiSelectedContent(
    navController: NavController,
    step: AiStep,
    answerMode: AnswerMode?,
    selectedTopic: String?,
    selectedCsTopics: Set<String>,
    selectedCsTopicNames: Set<String>,
    selectedJob: String?,
    selectedJobName: String?,
    selectedStack: String?,
    selectedStackName: String?,
    selectedLevel: String?,
    topicsState: AiSelectedVM.TopicsUiState,
    stackGroupsState: AiSelectedVM.StackGroupsUiState,
    stacksState: AiSelectedVM.StacksUiState,
    nextEnabled: Boolean,
    onAnswerModeSelected: (AnswerMode) -> Unit,
    onTopicSelected: (String) -> Unit,
    onCsTopicsChanged: (Set<String>, Set<String>) -> Unit,
    onJobSelected: (String, String) -> Unit,
    onStackSelected: (String, String) -> Unit,
    onLevelSelected: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg1)
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
                height = 48.dp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            when (step) {
                AiStep.ANSWER_MODE -> {
                    AnswerModeStep(
                        selectedMode = answerMode,
                        onSelected = onAnswerModeSelected
                    )
                }

                AiStep.STEP_1 -> {
                    Step1Content(
                        selectedTopic = selectedTopic,
                        onTopicSelected = onTopicSelected
                    )
                }

                AiStep.STEP_2 -> {
                    Step2Content(
                        state = topicsState,
                        selectedTopics = selectedCsTopics,
                        selectedTopicNames = selectedCsTopicNames,
                        onSelectedTopicsChanged = onCsTopicsChanged
                    )
                }

                AiStep.STEP_3 -> {
                    Step3Content(
                        state = stackGroupsState,
                        selectedJob = selectedJob,
                        onJobSelected = onJobSelected
                    )
                }

                AiStep.STEP_4 -> {
                    Step4Content(
                        state = stacksState,
                        selectedStack = selectedStack,
                        onStackSelected = onStackSelected
                    )
                }

                AiStep.STEP_5 -> {
                    Step5Content(
                        selectedLevel = selectedLevel,
                        onLevelSelected = onLevelSelected,
                        isCsMode = selectedTopic == "CS"
                    )
                }

                AiStep.STEP_6 -> {
                    Step6Content(
                        mode = if (answerMode == AnswerMode.Voice) {
                            "음성 면접"
                        } else {
                            "텍스트 면접"
                        },
                        topic = if (selectedTopic == "CS") {
                            "CS 개념모드"
                        } else {
                            selectedJobName ?: "직군 특화 모드"
                        },
                        selectedTopics = if (selectedTopic == "CS") {
                            selectedCsTopicNames.toList()
                        } else {
                            listOfNotNull(selectedStackName)
                        },
                        difficulty = selectedLevel ?: "중"
                    )
                }
            }
        }

        if (step == AiStep.ANSWER_MODE) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 20.dp
                    )
            ) {
                BlockButton(
                    state = if (nextEnabled) {
                        ButtonState.Default
                    } else {
                        ButtonState.Secondary
                    },
                    text = "다음",
                    style = AppTypography.Headline2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = {
                        if (nextEnabled) {
                            onNext()
                        }
                    }
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                AiPreviousNextButtons(
                    enabled = nextEnabled,
                    onPrevious = onPrevious,
                    onNext = onNext,
                    nextText = if (step == AiStep.STEP_6) "시작하기" else "다음"
                )
            }
        }
    }
}
@Composable
private fun AnswerModeStep(
    selectedMode: AnswerMode?,
    onSelected: (AnswerMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "답변 방식을 골라주세요.",
            color = AppColor.text1,
            style = AppTypography.Heading2
        )

        Spacer(Modifier.height(32.dp))

        AnswerModeItem(
            title = "음성 모드",
            description = "실제로 말하면서 답변을 연습해요.",
            icon = R.drawable.mic_icon,
            selected = selectedMode == AnswerMode.Voice,
            onClick = {
                onSelected(AnswerMode.Voice)
            }
        )

        Spacer(Modifier.height(12.dp))

        AnswerModeItem(
            title = "텍스트 모드",
            description = "직접 타이핑하면서 답변을 연습해요.",
            icon = R.drawable.text_icon,
            selected = selectedMode == AnswerMode.Text,
            onClick = {
                onSelected(AnswerMode.Text)
            }
        )
    }
}
@Composable
private fun AnswerModeItem(
    title: String,
    description: String,
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.lg))
            .background(
                if (selected) {
                    PrimitiveColor.Purple50
                } else {
                    AppColor.bg0
                }
            )
            .clickable(onClick = onClick)
            .padding(AppSpacing.spacing16),
        contentAlignment = Alignment.CenterStart
    ) {
        Row() {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(AppSpacing.spacing12))

            Column {
                Text(
                    text = title,
                    color = AppColor.text1,
                    style = AppTypography.Headline2
                )

                Spacer(Modifier.height(AppSpacing.spacing8))

                Text(
                    text = description,
                    color = AppColor.text3,
                    style = AppTypography.Label1
                )
            }
        }
    }
}
@Composable
private fun Step1Content(
    selectedTopic: String?,
    onTopicSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "면접에서 다룰 주제를 골라주세요.",
            color = AppColor.text1,
            style = AppTypography.Heading2
        )

        Spacer(Modifier.height(32.dp))

        AnswerModeItem(
            title = "CS (Computer Science)",
            description = "자료 구조, 알고리즘 등 CS 5개 분야 중\n원하는 주제를 골라 연습해요.",
            icon = R.drawable.cs_icon,
            selected = selectedTopic == "CS",
            onClick = {
                onTopicSelected("CS")
            }
        )

        Spacer(Modifier.height(12.dp))

        AnswerModeItem(
            title = "직군 특화 주제",
            description = "준비하는 직군에 맞춰 프레임워크, 언어까지\n심도 있게 물어봐요.",
            icon = R.drawable.job_icon,
            selected = selectedTopic == "JOB",
            onClick = {
                onTopicSelected("JOB")
            }
        )
    }
}
@Composable
private fun Step2Content(
    state: AiSelectedVM.TopicsUiState,
    selectedTopics: Set<String>,
    selectedTopicNames: Set<String>,
    onSelectedTopicsChanged: (Set<String>, Set<String>) -> Unit
) {
    val topics = when (state) {
        is AiSelectedVM.TopicsUiState.Success -> state.data
        else -> emptyList()
    }

    val allSelected =
        topics.isNotEmpty() &&
                selectedTopics.size == topics.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "연습할 CS 분야를 골라주세요.",
            color = AppColor.text1,
            style = AppTypography.Heading2
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "모든 분야를 다루고 싶다면 전체 선택을 눌러주세요",
            color = AppColor.text3,
            style = AppTypography.Caption1
        )

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = topics.isNotEmpty(),
                    indication = null,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }
                ) {
                    if (allSelected) {
                        onSelectedTopicsChanged(
                            emptySet(),
                            emptySet()
                        )
                    } else {
                        onSelectedTopicsChanged(
                            topics.map { it.topic }.toSet(),
                            topics.map { it.displayName }.toSet()
                        )
                    }
                },
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AiCheckBox(
                checked = allSelected
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = "전체 선택",
                color = AppColor.text1,
                style = AppTypography.Label1
            )
        }

        Spacer(Modifier.height(12.dp))

        when (state) {
            AiSelectedVM.TopicsUiState.Loading -> {
                CsTopicsSkeleton()
            }

            is AiSelectedVM.TopicsUiState.Success -> {
                state.data.forEach { topic ->
                    val isSelected = topic.topic in selectedTopics

                    val itemHeight by animateDpAsState(
                        targetValue = if (isSelected) 60.dp else 53.dp,
                        label = "itemHeight"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .clip(RoundedCornerShape(AppRadius.lg))
                            .background(AppColor.bg0)
                            .clickable {
                                if (isSelected) {
                                    onSelectedTopicsChanged(
                                        selectedTopics - topic.topic,
                                        selectedTopicNames - topic.displayName
                                    )
                                } else {
                                    onSelectedTopicsChanged(
                                        selectedTopics + topic.topic,
                                        selectedTopicNames + topic.displayName
                                    )
                                }
                            }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AiCheckBox(
                            checked = isSelected
                        )

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = topic.displayName,
                            color = AppColor.text1,
                            style = AppTypography.Headline2
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }

            else -> Unit
        }
    }
}
@Composable
private fun Step3Content(
    state: AiSelectedVM.StackGroupsUiState,
    selectedJob: String?,
    onJobSelected: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        AiStepIndicator(
            currentStep = 1
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "연습할 직군을 골라주세요.",
            color = AppColor.text1,
            style = AppTypography.Heading2
        )

        Spacer(Modifier.height(32.dp))

        when (state) {
            AiSelectedVM.StackGroupsUiState.Loading -> {
                StackGroupsSkeleton()
            }

            is AiSelectedVM.StackGroupsUiState.Success -> {
                state.data.forEach { job ->
                    AnswerModeItem(
                        title = job.displayName,
                        description = when (job.stackGroup) {
                            "SERVER" -> "Spring / Java 등"
                            "WEB" -> "React / TypeScript 등"
                            "AOS" -> "Jetpack / Kotlin"
                            "IOS" -> "SwiftUI / Swift"
                            else -> ""
                        },
                        icon = stackGroupIcon(job.stackGroup),
                        selected = selectedJob == job.stackGroup,
                        onClick = {
                            onJobSelected(
                                job.stackGroup,
                                job.displayName
                            )
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }

            else -> Unit
        }
    }
}
private fun stackGroupIcon(stackGroup: String): Int {
    return when (stackGroup) {
        "SERVER" -> R.drawable.server_icon
        "WEB" -> R.drawable.web_icon
        "AOS" -> R.drawable.aos_icon
        "IOS" -> R.drawable.ios_icon
        else -> R.drawable.job_icon
    }
}
@Composable
private fun Step4Content(
    state: AiSelectedVM.StacksUiState,
    selectedStack: String?,
    onStackSelected: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        AiStepIndicator(
            currentStep = 2
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "주로 다루는 스택을 골라주세요.",
            color = AppColor.text1,
            style = AppTypography.Heading2
        )

        Spacer(Modifier.height(32.dp))

        when (state) {
            AiSelectedVM.StacksUiState.Loading -> {
                StacksSkeleton()
            }

            is AiSelectedVM.StacksUiState.Success -> {
                state.data.forEach { stack ->
                    val (firstIcon, secondIcon) = stackIcons(stack.stack)

                    StackItem(
                        title = stack.displayName,
                        firstIcon = firstIcon,
                        secondIcon = secondIcon,
                        selected = selectedStack == stack.stack,
                        onClick = {
                            onStackSelected(
                                stack.stack,
                                stack.displayName
                            )
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }

            else -> Unit
        }
    }
}
private fun stackIcons(stack: String): Pair<Int, Int> {
    return when (stack) {
        "JAVA_SPRING_BOOT" ->
            R.drawable.java_icon to R.drawable.spring_icon

        "KOTLIN_SPRING_BOOT" ->
            R.drawable.kotlin_icon to R.drawable.spring_icon

        "NODE_NEST" ->
            R.drawable.nodeis_icon to R.drawable.spring_icon

        "PYTHON_DJANGO" ->
            R.drawable.python_icon to R.drawable.django_icon

        "TS_REACT" ->
            R.drawable.typescript_icon to R.drawable.react_icon

        "TS_VUE" ->
            R.drawable.typescript_icon to R.drawable.vue_icon

        "TS_NEXT" ->
            R.drawable.typescript_icon to R.drawable.nextjs_icon

        "KOTLIN_COMPOSE" ->
            R.drawable.kotlin_icon to  R.drawable.kotlin_icon

        "SWIFT_SWIFTUI" ->
            R.drawable.swift_icon to R.drawable.swiftui_icon

        else ->
            R.drawable.job_icon to R.drawable.job_icon
    }
}
@Composable
private fun StackItem(
    title: String,
    firstIcon: Int,
    secondIcon: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.lg))
            .background(
                if (selected) PrimitiveColor.Purple50
                else AppColor.bg0
            )
            .clickable(onClick = onClick)
            .padding(AppSpacing.spacing16),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(firstIcon),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )

        Spacer(Modifier.width(8.dp))

        Image(
            painter = painterResource(secondIcon),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = title,
            color = AppColor.text1,
            style = AppTypography.Headline2
        )
    }
}
@Composable
private fun Step5Content(
    selectedLevel: String?,
    onLevelSelected: (String) -> Unit,
    isCsMode: Boolean
) {
    val levels = listOf(
        "상" to "심화 개념까지 깊이 있게 물어봐요",
        "중" to "기본과 심화 개념을 골고루 물어봐요",
        "하" to "기본 개념 위주로 편하게 연습해요"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(20.dp))

        AiStepIndicator(
            currentStep = if (isCsMode) 2 else 3,
            isCsMode = isCsMode
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = "연습할 난이도를 골라주세요.",
            color = AppColor.text1,
            style = AppTypography.Heading2
        )

        Spacer(Modifier.height(32.dp))

        levels.forEach { (title, description) ->
            val isSelected = selectedLevel == title

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .clip(RoundedCornerShape(AppRadius.lg))
                    .background(
                        if (isSelected) {
                            PrimitiveColor.Purple50
                        } else {
                            AppColor.bg0
                        }
                    )
                    .clickable {
                        onLevelSelected(title)
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        color = AppColor.text1,
                        style = AppTypography.Headline2
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = description,
                        color = AppColor.text3w,
                        style = AppTypography.Label1
                    )
                }

                Spacer(Modifier.weight(1f))

                AiRadioButton(
                    selected = isSelected
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
@Composable
private fun Step6Content(
    questionCount: Int = 5,
    estimatedMinutes: Int = 10,
    mode: String = "텍스트 면접",
    topic: String = "CS 개념모드",
    selectedTopics: List<String>,
    difficulty: String
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "선택하신 내용을 확인해주세요.",
            color = AppColor.text1,
            style = AppTypography.Heading2,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(AppRadius.lg))
                    .background(AppColor.bg0)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AppColor.bg1)
                        .padding(
                            horizontal = 12.dp,
                            vertical = 2.dp
                        )
                ) {
                    Text(
                        text = "문항 수",
                        color = AppColor.text1,
                        style = AppTypography.Body2_Reading
                    )
                }

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = questionCount.toString(),
                        color = AppColor.Main2,
                        style = AppTypography.Title1
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "문항",
                        color = AppColor.text3,
                        style = AppTypography.Body1_Normal
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(AppRadius.lg))
                    .background(AppColor.bg0)
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AppColor.bg1)
                        .padding(
                            horizontal = 12.dp,
                            vertical = 2.dp
                        )
                ) {
                    Text(
                        text = "예상 소요시간",
                        color = AppColor.text1,
                        style = AppTypography.Body2_Reading
                    )
                }

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "약",
                        color = AppColor.text3,
                        style = AppTypography.Body1_Normal
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = estimatedMinutes.toString(),
                        color = AppColor.Main2,
                        style = AppTypography.Title1
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "분",
                        color = AppColor.text3,
                        style = AppTypography.Body1_Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppRadius.lg))
                .background(AppColor.bg0)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(PrimitiveColor.Purple50)
                    .padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    )
            ) {
                Text(
                    text = mode,
                    color = AppColor.CTA_hover,
                    style = AppTypography.Body1_Normal
                )
            }

            Spacer(Modifier.height(8.dp))

            SummaryRow(
                title = "모드",
                content = topic
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "주제",
                    color = AppColor.text3,
                    style = AppTypography.Headline2
                )

                Spacer(Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    selectedTopics.forEachIndexed { index, topic ->
                        Text(
                            text = topic,
                            color = AppColor.text1,
                            style = AppTypography.Body1_Normal
                        )

                        if (index != selectedTopics.lastIndex) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            SummaryRow(
                title = "난이도",
                content = difficulty
            )
        }

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppRadius.lg))
                .background(PrimitiveColor.Purple50)
                .padding(16.dp)
        ) {
            Text(
                text = "시작 전에 확인해주세요!",
                color = AppColor.text1,
                style = AppTypography.Headline2
            )
            Spacer(Modifier.height(8.dp))
            BulletText(text = "답변은 최대 1,000자까지 작성할 수 있어요")
            Spacer(Modifier.height(8.dp))
            BulletText(text = "권장 답변 시간은 2분이에요 (자동 제출되지 않아요)")
            Spacer(Modifier.height(8.dp))
            BulletText(text = "제출 후에는 다시 수정할 수 없어요")
        }
        Spacer(Modifier.height(30.dp))
    }
}
@Composable
private fun BulletText(
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = AppColor.text3,
            style = AppTypography.Body1_Normal
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = text,
            color = AppColor.text3,
            style = AppTypography.Body1_Normal,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryRow(
    title: String,
    content: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = AppColor.text3,
            style = AppTypography.Headline2
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = content,
            color = AppColor.text1,
            style = AppTypography.Body1_Normal
        )
    }
}
@Composable
private fun AiStepIndicator(
    currentStep: Int,
    isCsMode: Boolean = false
) {
    val steps = if (isCsMode) {
        listOf("분야 선택", "난이도 선택")
    } else {
        listOf("직군 선택", "스택 선택", "난이도 선택")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, title ->
            val step = index + 1
            val selected = step == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) {
                                AppColor.Main1
                            } else {
                                AppColor.divider1
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.toString(),
                        color = Color.White,
                        style = AppTypography.Label1
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = title,
                    color = if (selected) {
                        AppColor.Main1
                    } else {
                        AppColor.text3
                    },
                    style = AppTypography.Label1
                )
            }

            if (index < steps.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp)
                        .width(24.dp),
                    thickness = 1.dp,
                    color = AppColor.divider1
                )
            }
        }
    }
}
@Composable
private fun AiRadioButton(
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .border(
                width = 1.dp,
                color = if (selected) {
                    AppColor.Main1
                } else {
                    AppColor.text3
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = AppColor.Main1,
                        shape = CircleShape
                    )
            )
        }
    }
}
@Composable
private fun AiPreviousNextButtons(
    enabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    nextText: String = "다음"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BlockButton(
                text = "이전",
                onClick = onPrevious,
                state = ButtonState.Stroke,
                style = AppTypography.Headline2,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            )

            BlockButton(
                text = nextText,
                onClick = {
                    if (enabled) {
                        onNext()
                    }
                },
                state = if (enabled) {
                    ButtonState.Default
                } else {
                    ButtonState.Secondary
                },
                style = AppTypography.Headline2,
                modifier = Modifier
                    .weight(3f)
                    .height(48.dp)
            )
        }
    }
}
@Composable
private fun AiCheckBox(
    checked: Boolean
) {
    val size by animateDpAsState(
        targetValue = if (checked) 18.dp else 16.dp,
        label = "checkboxSize"
    )

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(2.dp))
            .background(
                if (checked) {
                    AppColor.Main1
                } else {
                    Color.Transparent
                }
            )
            .border(
                width = if (checked) 0.dp else 1.5.dp,
                color = AppColor.text3,
                shape = RoundedCornerShape(2.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
