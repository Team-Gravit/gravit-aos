package com.example.gravit.main.Chapter.Lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.ProblemResultItem
import com.example.gravit.api.Problems
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.navigateTo
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.launch
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    navController: NavController,
    chapterId: Int,
    chapterName: String,
    unitId: Int,
    lessonId: Int
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

    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

    LaunchedEffect(lessonId) {
        resultsMap.clear()
        wrongAttempts.clear()
        vm.load(lessonId)
    }

    val ui by vm.state.collectAsState()
    when (ui) {
        LessonViewModel.UiState.SessionExpired -> {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        }
        LessonViewModel.UiState.Failed -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("문제를 불러오지 못했습니다.", fontFamily = pretendard)
            }
        }
        LessonViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        else -> Unit
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
        val prevWrong = wrongAttempts[problemId] ?: 0
        val newWrong = if (isCorrect) prevWrong else prevWrong + 1
        if (!isCorrect) wrongAttempts[problemId] = newWrong

        resultsMap[problemId] = ProblemResultItem(
            problemId = problemId,
            isCorrect = isCorrect,
            incorrectCounts = newWrong
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
        vm.submitResults(
            chapterId = chapterId,
            unitId = unitId,
            lessonId = lessonId,
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
            ) {
                key(index) {
                    if (current.options == "-") {
                        //주관식
                        ShortAnswer(
                            problem = current,
                            problemNum = index + 1,
                            totalProblems = total,
                            text = shortText,
                            submitted = submitted,
                            onTextChange = { shortText = it },
                            modifier = Modifier.fillMaxSize(),
                            isCorrect = lastCorrect,
                            onSubmit = {
                                val correct = isAnswerCorrect(shortText, current.answer)
                                lastCorrect = correct
                                submitted = true
                                recordResult(current.problemId, correct)
                                if (isLast) {
                                    finishLesson()
                                    navController.navigateTo(
                                        chapterId = chapterId,
                                        chapterName = chapterName,
                                        unitId = unitId,
                                        lessonId = lessonId,
                                        togo = "lesson/complete"
                                    )
                                }
                            },
                            isLast = isLast,
                            onNext = {
                                if (!isLast) {
                                    index++
                                    submitted = false
                                    shortText = ""
                                    lastCorrect = null
                                }
                            }
                        )
                    } else {
                        //객관식
                        MultipleChoice(
                            problem = current,
                            problemNum = index + 1,
                            totalProblems = total,
                            selectedIndex = selectedIndex,
                            submitted = submitted,
                            isCorrect = lastCorrect,
                            onSelect = { selectedIndex = it },
                            onSubmit = { selectedAnswerStr ->
                                val correct = isAnswerCorrect(selectedAnswerStr, current.answer)
                                lastCorrect = correct
                                submitted = true
                                recordResult(current.problemId, correct)
                                if (isLast) {
                                    finishLesson()
                                    navController.navigateTo(
                                        chapterId = chapterId,
                                        chapterName = chapterName,
                                        unitId = unitId,
                                        lessonId = lessonId,
                                        togo = "lesson/complete"
                                    )
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
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                }

            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch {
                        sheetState.hide()
                        showSheet = false
                    }
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.65f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.study_popup),
                        contentDescription = "학습 중단 팝업 일러",
                        modifier = Modifier.padding(20.dp)
                    )
                    Text(
                        "지금까지 푼 내역이\n모두 사라져요!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = pretendard,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "${chapterName} 학습출제가 중단됩니다.\n정말 학습을 그만두시나요?",
                        fontSize = 16.sp,
                        fontFamily = pretendard,
                        color = Color(0xFF6D6D6D),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                                showSheet = false
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
                            "계속하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = pretendard
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "그만두기",
                        color = Color(0xFF6D6D6D),
                        fontFamily = pretendard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showSheet = false
                                    navController.navigate("home"){
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
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