package com.example.gravit.main.Chapter.Lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.ProblemResultItem
import com.example.gravit.api.Problems
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.toLessonCompleted
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    navController: NavController,
    chapterId: Int,
    chapterName: String,
    unitId: Int,
    lessonId: Int,
    onSessionExpired: () -> Unit
) {
    //스톱워치
    val swVm: StopwatchViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        // 앱이 백그라운드로 가면 멈춤
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                swVm.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)

        // 컴포저블이 사라질 때(네비게이션 이동 등)도 멈춤
        onDispose {
            swVm.pause()
            lifecycleOwner.lifecycle.removeObserver(obs)
        }
    }

    val resultsMap = remember { mutableStateMapOf<Int, ProblemResultItem>() }
    val wrongAttempts = remember { mutableStateMapOf<Int, Int>() }
    var resultNext by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

    LaunchedEffect(lessonId) {
        resultsMap.clear()
        wrongAttempts.clear()
        vm.load(lessonId)
        vm.resetSubmit()
    }

    val ui by vm.state.collectAsState()
    val submit by vm.submit.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(ui) {
        when (ui) {
            LessonViewModel.UiState.SessionExpired ->  {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.UiState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.UiState.Failed -> {
                navigated = true
                onSessionExpired()
            }
            else -> Unit
        }
    }

    LaunchedEffect(submit) {
        when (submit) {
            LessonViewModel.SubmitState.SessionExpired ->  {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.SubmitState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.SubmitState.Failed -> {
                navigated = true
                onSessionExpired()
            }
            else -> Unit
        }
    }
    var showLoading by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000)
        showLoading = false
    }
    if (showLoading) {
        LoadingScreen()
        return
    }

    val s = ui as? LessonViewModel.UiState.Success?: return
    val problems = s.data.problems
    val declaredTotal = s.data.totalProblems
    val total = if (declaredTotal > 0) min(declaredTotal, problems.size) else problems.size

    val problemSlots: List<Problems> = remember(problems, total) {
        List(total) { idx -> problems[idx] }
    }

    //기록
    fun recordResult(problemId: Int, isCorrect: Boolean) {
        val wrongAttempts = if (isCorrect) 0 else 1

        resultsMap[problemId] = ProblemResultItem(
            problemId = problemId,
            isCorrect = isCorrect,
            incorrectCounts = wrongAttempts
        )
    }
    var submitting by remember { mutableStateOf(false) }

    //제출
    fun finishLesson() {
        swVm.pause()
        if (submitting) return
        submitting = true

        val payload = resultsMap.values.toList()
        if (payload.isEmpty()) {
            submitting = false
            return
        }
        val learningTime = (swVm.state.value.elapsedMillis / 1000).toInt()
        val correctCount = payload.count { it.isCorrect }
        val accuracy: Int = if(total > 0) { ((correctCount.toDouble()/total) * 100).roundToInt() } else 0

        resultNext = accuracy to learningTime

        vm.submitResults(
            lessonId = lessonId,
            learningTime = learningTime,
            accuracy = accuracy,
            results = payload
        ) { ok ->
            submitting = false
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    var index by rememberSaveable(total) { mutableStateOf(0) }
    var submitted by remember(index) { mutableStateOf(false) }
    var selectedIndex by remember(index) { mutableStateOf<Int?>(null) }
    var shortText by remember(index) { mutableStateOf("") }
    var lastCorrect by remember(index) { mutableStateOf<Boolean?>(null) }

    val current = problemSlots[index]
    val isLast = index == total - 1

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .background(Color(0xFFF2F2F2))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.White)
            ) {
                Text(
                    text = chapterName,
                    fontSize = 20.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF030303)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                swVm.pause()
                                coroutineScope.launch {
                                    showSheet = true
                                    sheetState.show()
                                }
                            },
                        tint = Color(0xFF494949)
                    )
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_timer_24),
                            contentDescription = "stopwatch",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF494949)

                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Stopwatch(
                            vm = swVm,
                            autoStart = true,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0xFFF1CCFF))
            ) {
                val safeTotal = total.coerceAtLeast(1)
                val progress = ((index + 1).coerceAtMost(safeTotal)).toFloat() / safeTotal

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(Color(0xFFBA01FF))
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Column (modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.clipboard),
                            contentDescription = "clipboard",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${index+1}/${total}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = pretendard,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        ReportDialog(navController = navController, problemId = current.problemId)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = current.question,
                        fontSize = 16.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF383838)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFDCDCDC),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(Color.White)
                    ) {
                        InlineUnderlineText(
                            raw = current.content,
                            modifier = Modifier.padding(10.dp),
                            fontSize = 16.sp,
                            fontFamily = pretendard,
                            color = Color.Black,
                            strokeWidth = 1.dp
                        )
                    }
                }
            }
            Spacer(modifier=Modifier.height(30.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if(current.problemType == "SUBJECTIVE") {
                    ShortAnswer(
                        submitted = submitted,
                        problemId = current.problemId,
                        text = shortText,
                        answer = current.answer,
                        onTextChange = { shortText = it },
                        isCorrect = lastCorrect,
                        onSubmit = {
                            val correct = isAnswerCorrect(shortText, current.answer)
                            lastCorrect = correct
                            submitted = true
                            recordResult(current.problemId, correct)
                            if (isLast) {
                                finishLesson()
                            }
                        },
                        isLast = isLast,
                        onNext = {
                            if (!isLast) {
                                index++
                                submitted = false
                                selectedIndex = null
                                shortText = ""
                                lastCorrect = null
                            } else{
                                when (submit) {
                                    is LessonViewModel.SubmitState.Success -> {
                                        navController.toLessonCompleted(
                                            chapterId = chapterId,
                                            chapterName = chapterName,
                                            unitId = unitId,
                                            lessonId = lessonId,
                                            accuracy = resultNext?.first ?: 0,
                                            learningTime = resultNext?.second ?: 0
                                        )
                                    }
                                    else ->  Unit
                                }
                            }
                        },
                    )
                } else{
                    MultipleChoice(
                        options = current.options,
                        problemNum = current.problemId,
                        selectedIndex = selectedIndex,
                        submitted = submitted,
                        isCorrect = lastCorrect,
                        onSelect = { selectedIndex = it },
                        onSubmit = { selectedIdx ->
                            val correct = current.options.getOrNull(selectedIdx)?.isAnswer == true
                            lastCorrect = correct
                            submitted = true
                            recordResult(current.problemId, correct)
                            if (isLast) {
                                finishLesson()
                            }
                        },
                        isLast = isLast,
                        onNext = {
                            if (!isLast) {
                                index++
                                submitted = false
                                selectedIndex = null
                                shortText = ""
                                lastCorrect = null
                            } else{
                                when (submit) {
                                    is LessonViewModel.SubmitState.Success -> {
                                        navController.toLessonCompleted(
                                            chapterId = chapterId,
                                            chapterName = chapterName,
                                            unitId = unitId,
                                            lessonId = lessonId,
                                            accuracy = resultNext?.first ?: 0,
                                            learningTime = resultNext?.second ?: 0
                                        )
                                    }
                                    else ->  Unit
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        if (showSheet) {
            ConfirmBottomSheet(
                onDismiss = { showSheet = false },
                imageRes = R.drawable.study_popup,
                titleText = "지금까지 푼 내역이\n모두 사라져요!",
                descriptionText = "${chapterName} 학습출제가 중단됩니다.\n정말 학습을 그만두시나요?",
                confirmButtonText = "계속하기",
                cancelText = "그만두기",
                onConfirm = {
                },
                onCancel = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        if (ui is LessonViewModel.UiState.Loading || submit is LessonViewModel.SubmitState.Loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

fun isAnswerCorrect(userAnswer: String?, correctAnswer: String?): Boolean {
    if (correctAnswer.isNullOrBlank()) return false
    if (userAnswer.isNullOrBlank()) return false

    //숫자 비교
    val userNum = userAnswer.replace(",", "").toBigDecimalOrNull()
    val corrNum = correctAnswer.replace(",", "").toBigDecimalOrNull()
    if (userNum != null && corrNum != null) {
        return userNum.compareTo(corrNum) == 0
    }

    // 문자열 정규화
    fun norm(s: String) = s.trim()
        .lowercase()
        .replace(Regex("\\s+"), " ")  // 여러 공백 → 하나
        .replace(Regex("[.,]"), "")   // 쉼표/마침표 무시

    //중복 답 분리
    val candidates = correctAnswer
        .split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    val userN = norm(userAnswer)

    if (candidates.isNotEmpty()) {
        return candidates.any { norm(it) == userN }
    }

    return norm(correctAnswer) == userN
}

@Composable
fun InlineUnderlineText(
    raw: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    fontFamily: FontFamily? = null,
    color: Color = Color.Black,
    strokeWidth: Dp = 2.dp
) {

    val matches = remember(raw) { Regex("_+").findAll(raw).toList() }

    val annotated = remember(raw) {
        buildAnnotatedString {
            var cur = 0
            matches.forEachIndexed { i, m ->
                if (cur < m.range.first) append(raw.substring(cur, m.range.first))
                appendInlineContent("blank$i", "[blank]")
                cur = m.range.last + 1
            }
            if (cur < raw.length) append(raw.substring(cur))
        }
    }

    val inline = remember(raw, fontSize, strokeWidth) {
        matches.mapIndexed { i, m ->
            val count = m.value.length
            "blank$i" to InlineTextContent(
                Placeholder(
                    width = count * fontSize * 0.60f,
                    height = fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val y = size.height - strokeWidth.toPx() / 2f
                            drawLine(
                                color = color,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth.toPx()
                            )
                        }
                )
            }
        }.toMap()
    }

    Text(
        modifier = modifier,
        text = annotated,
        inlineContent = inline,
        fontSize = fontSize,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium,
        color = color,
        lineHeight = fontSize * 1.3f,
        style = LocalTextStyle.current.copy(
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBottomSheet(
    onDismiss: () -> Unit,
    imageRes: Int? = null,   // 🔥 여기서 imageRes를 옵션으로 추가
    titleText: String,
    descriptionText: String,
    confirmButtonText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = Color.White
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔥 imageRes가 null이 아닐 때만 보여줌
            imageRes?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = pretendard,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = descriptionText,
                fontSize = 16.sp,
                fontFamily = pretendard,
                color = Color(0xFF6D6D6D),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    onConfirm()
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8100B3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    confirmButtonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = pretendard
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = cancelText,
                color = Color(0xFF6D6D6D),
                fontFamily = pretendard,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onCancel()
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}
