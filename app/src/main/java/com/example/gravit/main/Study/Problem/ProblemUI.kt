package com.inuappcenter.gravit.main.Study.Problem

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.main.Study.Problem.ProblemViewModel
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.api.AnswerResponse
import com.inuappcenter.gravit.api.Problems
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.main.ConfirmDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemUI(
    navController: NavController,
    unitTitle: String,
    problems: List<Problems>,
    total: Int,
    swVm: StopwatchViewModel,
    bookmarkMap: Map<Long, Boolean>,
    onBookmarkToggle: (Long) -> Unit,
    onRecordResult: (problemId: Long, isCorrect: Boolean, selectedOptionId: Long?, submittedContent: String?) -> Unit,
    onFinishLesson: () -> Unit,
    type: String = "normal",
    onRemoveWrongNote: (Long) -> Unit = {},
    unitId: Long
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var bookmarkSnackBar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(bookmarkSnackBar) {
        if (bookmarkSnackBar != null) {
            delay(2000)
            bookmarkSnackBar = null
        }
    }
    val problemVm: ProblemViewModel = viewModel()
    val state by problemVm.uiState.collectAsState()

    var index by rememberSaveable { mutableIntStateOf(0) }
    if (problems.isEmpty()) {
        LaunchedEffect(Unit) {
            onFinishLesson()
        }
        return
    }
    val safeIndex = index.coerceIn(0, problems.lastIndex)
    val current = problems[safeIndex]
    val isLast = safeIndex == problems.lastIndex
    LaunchedEffect(problems.size) {
        if (index != safeIndex) {
            index = safeIndex
        }
    }

    val currentAnswer = state.answers[current.problemId]?: ProblemViewModel.AnswerState()

    val isBookmark = bookmarkMap[current.problemId] ?: false

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val isSubjective = current.problemType == "SUBJECTIVE"
    val canSubmit = if (isSubjective) currentAnswer.shortText.isNotBlank() else currentAnswer.selectedIndex != null
    val nextButtonEnabled = currentAnswer.submitted || canSubmit
    val submitCurrentProblem: () -> Unit = {
        if (isSubjective) {
            val correct = isAnswerCorrect(currentAnswer.shortText, current.answerResponse)
            problemVm.submit(problemId = current.problemId, isCorrect = correct)
            onRecordResult(current.problemId, correct, null, currentAnswer.shortText)
        } else {
            val selectedIdx = currentAnswer.selectedIndex
            if (selectedIdx != null) {
                val selectedOption = current.options.getOrNull(selectedIdx)
                if (selectedOption != null) {
                    val correct = selectedOption.isAnswer
                    problemVm.submit(problemId = current.problemId, isCorrect = correct)
                    onRecordResult(current.problemId, correct, selectedOption.optionId, null)
                }
            }
        }
    }
    val moveToNextProblem: () -> Unit = {
        if (currentAnswer.submitted) {
            if (!isLast) {
                index++
            } else {
                onFinishLesson()
            }
        }
    }

    Log.d("can",canSubmit.toString())
    BackHandler(enabled = true) {
        if (showSheet) {
            showSheet = false
            swVm.start()
            return@BackHandler
        }

        focusManager.clearFocus(force = true)
        keyboard?.hide()

        swVm.pause()
        if (type == "normal") {
            showSheet = true
        } else {
            navController.navigate("lessonList/$unitId") {
                popUpTo("lessonList/$unitId") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg1),
        ) {
            //헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg0)
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        contentDescription = "닫기",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(24.dp)
                            .clickable {
                                swVm.pause()
                                if (type == "normal") {
                                    showSheet = true
                                } else {
                                    navController.navigate("lessonList/$unitId") {
                                        popUpTo("lessonList/$unitId") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                        tint = AppColor.icon_default
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        painter = painterResource(id = R.drawable.timer),
                        contentDescription = "stopwatch",
                        modifier = Modifier.size(20.dp),
                        tint = AppColor.Main1

                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Stopwatch(
                        vm = swVm,
                        autoStart = true
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    ReportDialog(
                        navController = navController,
                        problemId = current.problemId,
                        onOverlayOpened = { swVm.pause() },
                        onOverlayClosed = { swVm.start() },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(PrimitiveColor.Purple100)
            ) {
                val safeTotal = total.coerceAtLeast(1)
                val progress = ((index + 1).coerceAtMost(safeTotal)).toFloat() / safeTotal

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(AppColor.Main1)
                )
            }
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 250.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0)
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column{
                        Row{
                            Text(
                                text = (index + 1).toString().padStart(2, '0'),
                                style = AppTypography.Heading1,
                                color = AppColor.text1
                            )
                            Spacer(Modifier.weight(1f))
                            Image(
                                painter = painterResource(
                                    if (isBookmark) R.drawable.bookmark_on else R.drawable.bookmark_off
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        bookmarkSnackBar = if (!isBookmark) {
                                            "북마크에 추가되었어요."
                                        } else {
                                            "북마크에서 제거되었어요."
                                        }
                                        onBookmarkToggle(current.problemId)

                                    }
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = current.instruction,
                            style = AppTypography.Headline1,
                            color = AppColor.text1
                        )
                        Spacer(Modifier.height(20.dp))
                        InlineUnderlineText(
                            raw = current.content,
                            style = AppTypography.Body2_Reading.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            strokeWidth = 1.dp,
                            color = AppColor.text1
                        )
                    }
                }
            }
            Spacer(modifier=Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .weight(1f)
            ) {
                if(current.problemType == "SUBJECTIVE") {
                    ShortAnswer(
                        submitted = currentAnswer.submitted,
                        isCorrect = currentAnswer.isCorrect,
                        problemId = current.problemId,
                        text = currentAnswer.shortText,
                        answer = current.answerResponse,
                        onTextChange = { problemVm.updateText(current.problemId, it) },
                        showRemoveFromWrongNote = (type == "wrong-answered-notes"),
                        onRemoveFromWrongNote = { onRemoveWrongNote(current.problemId) },
                        problemVm = problemVm
                    )
                } else{
                    MultipleChoice(
                        options = current.options,
                        problemNum = current.problemId,
                        selectedIndex = currentAnswer.selectedIndex,
                        submitted = currentAnswer.submitted,
                        isCorrect = currentAnswer.isCorrect,
                        onSelect = { problemVm.select(current.problemId, it) },
                        showRemoveFromWrongNote = (type == "wrong-answered-notes"),
                        onRemoveFromWrongNote = { onRemoveWrongNote(current.problemId) },
                        problemVm = problemVm
                    )
                }
            }
            ReportButton(
                text1 = "이전",
                onClick1 = { if (index > 0) index-- },
                text2 = "다음",
                onClick2 = {
                    if (currentAnswer.submitted) {
                        moveToNextProblem()
                    } else {
                        submitCurrentProblem()
                    }
                },
                enabled1 = index != 0,
                enabled2 = nextButtonEnabled,
                modifier1 = Modifier.height(48.dp).weight(1f),
                modifier2 = Modifier.height(48.dp).weight(3f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
            )
        }
        if (bookmarkSnackBar != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 45.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                CustomSnackBar(text = bookmarkSnackBar!!)
            }
        }
        if (showSheet) {
            ConfirmDialog(
                onDismiss = {
                    coroutineScope.launch { sheetState.hide() }
                    showSheet = false
                    swVm.start()
                },
                imageRes = R.drawable.study_popup,
                titleText = "지금까지 푼 내역이\n모두 사라져요!",
                descriptionText = "$unitTitle 학습출제가 중단됩니다.\n정말 학습을 그만두시나요?",
                confirmButtonText = "계속하기",
                cancelButtonText = "그만두기",
                onConfirm = {
                    showSheet = false
                    swVm.start()
                },
                onCancel = {
                    showSheet = false
                    navController.navigate("lessonList/$unitId") {
                        popUpTo("lessonList/$unitId") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

    }
}

fun isAnswerCorrect(
    userAnswer: String?,
    correctAnswer: AnswerResponse?
): Boolean {
    if (correctAnswer?.contents.isNullOrEmpty()) return false
    if (userAnswer.isNullOrBlank()) return false

    fun norm(s: String) = s.trim()
        .lowercase()
        .replace(Regex("\\s+"), "")

    val userN = norm(userAnswer)

    return correctAnswer.contents.any { answer ->
        val ansN = norm(answer)
        userN == ansN
    }
}

@Composable
fun InlineUnderlineText(
    raw: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
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

    val inline = remember(raw, style.fontSize, strokeWidth) {
        matches.mapIndexed { i, m ->
            val count = m.value.length
            "blank$i" to InlineTextContent(
                Placeholder(
                    width = count * style.fontSize * 0.60f,
                    height = style.fontSize,
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
        style = style,
        color = color
    )
}

@Composable
fun CustomSnackBar(
    text: String,
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimitiveColor.Gray1100,
            contentColor = AppColor.text1w
        ),
    ){
        Box(
            modifier = Modifier.size(173.dp, 41.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = AppTypography.Label1,
                color = AppColor.text1w
            )
        }
    }
}
