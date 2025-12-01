package com.example.gravit.main.Study.Problem

import android.text.TextUtils.replace
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.gravit.api.AnswerResponse
import com.example.gravit.api.LessonSubmissionSaveRequest
import com.example.gravit.api.ProblemSubmissionRequests
import com.example.gravit.api.Problems
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.ConfirmBottomSheet
import com.example.gravit.navigation.toLessonCompleted
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemUI(
    navController: NavController,
    chapterName: String,
    problems: List<Problems>,
    total: Int,
    swVm: StopwatchViewModel,
    bookmarkMap: Map<Int, Boolean>,
    onBookmarkToggle: (Int) -> Unit,
    onRecordResult: (problemId: Int, isCorrect: Boolean) -> Unit,
    onFinishLesson: () -> Unit,
    type: String = "normal",
    onRemoveWrongNote: (Int) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    var bookmarkSnackBar by remember { mutableStateOf(false) }

    LaunchedEffect(bookmarkSnackBar) {
        if (bookmarkSnackBar) {
            delay(2000)
            bookmarkSnackBar = false
        }
    }

    var index by rememberSaveable(total) { mutableStateOf(0) }
    var submitted by remember(index) { mutableStateOf(false) }
    var selectedIndex by remember(index) { mutableStateOf<Int?>(null) }
    var shortText by remember(index) { mutableStateOf("") }
    var lastCorrect by remember(index) { mutableStateOf<Boolean?>(null) }

    val current = problems[index]
    val isLast = index == total - 1
    val isBookmark = bookmarkMap[current.problemId] ?: false

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .background(Color(0xFFF2F2F2))
        ) {
            //헤더
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
            //진행도 바
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

            //문제 설명
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
                            painter = painterResource(
                                if (isBookmark) R.drawable.bookmark_on else R.drawable.bookmark_off
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    if(!isBookmark){
                                        bookmarkSnackBar = true
                                    }
                                    onBookmarkToggle(current.problemId)
                                }
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
                        ReportDialog(
                            navController = navController,
                            problemId = current.problemId
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = current.instruction,
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

            //객관식, 주관식
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
                        answer = current.answerResponse,
                        onTextChange = { shortText = it },
                        isCorrect = lastCorrect,
                        onSubmit = {
                            val correct = isAnswerCorrect(shortText, current.answerResponse)
                            lastCorrect = correct
                            submitted = true

                            onRecordResult(current.problemId, correct)
                        },

                        isLast = isLast,
                        onNext = {
                            if (!isLast) {
                                index++
                                submitted = false
                                selectedIndex = null
                                shortText = ""
                                lastCorrect = null
                            } else {
                                onFinishLesson()
                            }
                        },
                        showRemoveFromWrongNote = (type == "wrong-answered-notes"),
                        onRemoveFromWrongNote = {
                            onRemoveWrongNote(current.problemId)
                        }
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
                            val correct =
                                current.options.getOrNull(selectedIdx)?.isAnswer == true
                            lastCorrect = correct
                            submitted = true

                            onRecordResult(current.problemId, correct)
                        },
                        isLast = isLast,
                        onNext = {
                            if (!isLast) {
                                index++
                                submitted = false
                                selectedIndex = null
                                shortText = ""
                                lastCorrect = null
                            } else {
                                onFinishLesson()
                            }
                        },
                        showRemoveFromWrongNote = (type == "wrong-answered-notes"),
                        onRemoveFromWrongNote = {
                            onRemoveWrongNote(current.problemId)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        if(bookmarkSnackBar){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 19.dp),
                contentAlignment = Alignment.BottomCenter
            ){
                CustomSnackBar()
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

    }
}

fun isAnswerCorrect(userAnswer: String?, correctAnswer: AnswerResponse): Boolean {
    if (correctAnswer.content.isBlank()) return false
    if (userAnswer.isNullOrBlank()) return false

    //숫자 비교
    val userNum = userAnswer.replace(",", "").toBigDecimalOrNull()
    val corrNum = correctAnswer.content.replace(",", "").toBigDecimalOrNull()
    if (userNum != null && corrNum != null) {
        return userNum.compareTo(corrNum) == 0
    }

    // 문자열 정규화
    fun norm(s: String) = s.trim()
        .lowercase()
        .replace(Regex("\\s+"), " ")  // 여러 공백 → 하나
        .replace(Regex("[.,]"), "")   // 쉼표/마침표 무시

    //중복 답 분리
    val candidates = correctAnswer.content
        .split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    val userN = norm(userAnswer)

    if (candidates.isNotEmpty()) {
        return candidates.any { norm(it) == userN }
    }

    return norm(correctAnswer.content) == userN
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

@Composable
fun CustomSnackBar(){
    Card(
        shape = RoundedCornerShape(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f),
            contentColor = Color.White
        ),
    ){
        Box(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "북마크에 추가되었어요.",
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
