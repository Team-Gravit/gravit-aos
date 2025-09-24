package com.example.gravit.main.Chapter.Lesson

import android.R.attr.displayOptions
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.request.Options
import com.example.gravit.R
import com.example.gravit.Responsive
import com.example.gravit.api.OptionDto
import com.example.gravit.ui.theme.pretendard
import kotlin.collections.mapIndexed

@Composable
fun MultipleChoice(
    options: List<OptionDto>,
    problemNum: Int,
    selectedIndex: Int?,
    submitted: Boolean,
    onSelect: (Int?) -> Unit,
    onSubmit: (String) -> Unit,
    isLast: Boolean,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    isCorrect: Boolean?
) {
    var showCompleteButton by remember(problemNum) { mutableStateOf(false) }
    var readyToSubmit     by remember(problemNum) { mutableStateOf(false) }

    // 선택 해제되면 버튼 숨김
    LaunchedEffect(selectedIndex, submitted) {
        if (selectedIndex == null || submitted) {
            showCompleteButton = false
            readyToSubmit = false
        }
    }

    val mcOptions = remember(options) {
        options.mapIndexed { idx, o ->
            MCOption(
                badge = (idx + 1).toString(),
                text = o.content,
                explanation = o.explanation,
                isAnswer = o.isAnswer
            )
        }
    }
    val displayOptions = remember(mcOptions) {
        if (mcOptions.size >= 4) mcOptions.take(4)
        else mcOptions + List(4 - mcOptions.size) {
            MCOption(
                badge = (mcOptions.size + it + 1).toString(),
                text = "",
                explanation = null,
                isAnswer = false
            )
        }
    }

    // 정답 인덱스는 화면에 실제로 그리는 리스트 기준으로
    val correctIdx = remember(displayOptions) {
        displayOptions.indexOfFirst { it.isAnswer }.takeIf { it >= 0 }
    }

    // 오답 설명 노출 인덱스
    val showExplanation = remember(submitted, selectedIndex, correctIdx) {
        if (submitted && selectedIndex != null && selectedIndex != correctIdx) selectedIndex else null
    }

    val showEye = (selectedIndex == null)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            displayOptions.forEachIndexed { idx, opt ->
                val isSelected = selectedIndex == idx
                val isRight    = submitted && idx == correctIdx
                val isWrong    = submitted && isSelected && idx != correctIdx
                val enabled    = !submitted && opt.text.isNotBlank()

                val explanationToShow = if (idx == showExplanation) {
                    opt.explanation?.takeUnless { it.isBlank() || it.equals("null", true) }
                } else null

                OptionCell(
                    num = opt.badge,
                    answer = opt.text,
                    isSelected = isSelected,
                    isRight = isRight,
                    isWrong = isWrong,
                    enabled = enabled,
                    showEye = showEye,
                    explanation = explanationToShow,
                    onClick = {
                        if (!enabled) return@OptionCell
                        val newSel: Int? = if (isSelected) null else idx
                        onSelect(newSel)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

data class MCOption(
    val badge: String,
    val text: String,
    val explanation: String?,
    val isAnswer: Boolean
)
@Composable
private fun OptionCell(
    num: String,
    answer: String,
    isSelected: Boolean,
    isRight: Boolean,
    isWrong: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    showEye: Boolean,
    explanation: String?
) {
    var isShown by remember { mutableStateOf(true) }
    val rowAlpha = if (showEye && !isSelected && !isShown) 0.4f else 1f

    Column (
        modifier = modifier
            .alpha(rowAlpha)
            .clickable(enabled = enabled) { onClick() }
            .background(if (isSelected) Color(0xFFDCDCDC) else Color(0xFFF2F2F2)),
        verticalArrangement = Arrangement.Center
    ){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        when {
                            isRight -> Color(0xFF00A80B)
                            isWrong -> Color(0xFFFF0000)
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color(0xFF6D6D6D), RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center

            ) {
                Text(
                    text = when {
                        isWrong -> "X"
                        isRight -> "V"
                        else -> num
                    },
                    color = when {
                        isRight -> Color.White
                        isWrong -> Color.White
                        isSelected -> Color.Black
                        else -> Color(0xFF6D6D6D)
                    }
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = answer,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                fontSize = 16.sp,
                color = when {
                    isRight -> Color(0xFF00A80B)
                    isWrong -> Color(0xFFFF0000)
                    else -> Color(0xFF6D6D6D)
                },
                modifier = Modifier.weight(1f)
            )

            if (showEye && !isSelected) {
                Icon(
                    painter = painterResource(id = if (isShown) R.drawable.eye else R.drawable.close_eye),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = Responsive.w(20f))
                        .size(24.dp)
                        .clickable { isShown = !isShown } // 토글
                )
            }

        }
        if (!explanation.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "DFS: 탐색 알고리즘의 한 종류.\u2028실행할 때 스택 구조를 사용하긴 하지만,\u2028자료구조의 이름이 아니라 탐색 방식이기 때문에 정답이 될 수 없어요.",
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color(0xFFD00000),
                modifier = Modifier.padding(start = 76.dp, end = 20.dp)
            )
        }
    }
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
fun ReportDialog(
    modifier: Modifier = Modifier
){
    var showDialog by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    Image(
        painter = painterResource(id = R.drawable.report),
        contentDescription = "report",
        modifier = modifier
            .size(24.dp)
            .clickable { showDialog = true }
    )

    if(showDialog){
        var text by remember { mutableStateOf("") }
        var selectedIndex by remember { mutableStateOf<Int?>(null) }
        val options = listOf(
            "문제/선지에 오탈자가 있습니다.",
            "문제 자체에 오류가 있습니다.",
            "답안에 오류가 있습니다.",
            "기타"
        )
        val canSubmit = selectedIndex != null
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) {
            Surface(
                modifier = Modifier.height(497.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Column (modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.report_color),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "신고하기",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Column(Modifier.padding(vertical = 16.dp)) {
                        options.forEachIndexed { idx, label ->
                            Option(
                                text = label,
                                onClick = { selectedIndex = if (selectedIndex == idx) null else idx}
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = {
                                Text(
                                    text = "어떤 부분에 문제가 있었는지 최대한 자세하게 작성해 주세요.\n유형이 두 개 이상인 경우, 기타를 선택하고 자세하게 작성해 주세요.",
                                    color = Color(0xFF868686),
                                    fontFamily = pretendard
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(102.dp),
                            shape = RoundedCornerShape(10.dp),
                        )
                    }
                    Row {
                        ReportButton(
                            onClick = { showDialog = false },
                            text = "그만두기",
                            bgC = Color(0xFFA8A8A8),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(19.dp))
                        ReportButton(
                            onClick = {
                                showDialog = false
                                showConfirm = true },
                            text = "제출하기",
                            bgC = Color(0xFF8100B3),
                            modifier = Modifier.weight(1f),
                            enabled = canSubmit
                        )
                    }
                }
            }
        }
    }

    if(showConfirm && !showDialog){
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) {
            Surface(
                modifier = Modifier.height(294.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 43.dp),
                    horizontalAlignment = Alignment.CenterHorizontally)
                {
                    Image(
                        painter = painterResource(id = R.drawable.check_circle),
                        contentDescription = null,

                        )
                    Spacer(Modifier.height(23.dp))
                    Text(
                        text = "회원님의 신고가 접수되었어요.",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "회원님의 소중한 의견들을 모아\n더욱 쾌적한 앱 환경을 만들겠습니다.\n단, 허위로 신고할 경우 제재 대상이 될 수 있어요.",
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color(0xFF868686),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(28.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = { showConfirm = false },
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF8100B3)
                        )
                    ) {
                        Text(
                            text = "확인",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun ReportButton(
    text: String,
    bgC: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxHeight(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgC,
            contentColor = Color.White,
            disabledContainerColor = bgC,
            disabledContentColor = Color.White
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            fontFamily = pretendard,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}
@Composable
fun Option(
    text : String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 퍼지는 효과 끔
            ) { onClick() }
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF2F2F2)),
        verticalAlignment = Alignment.CenterVertically
    ){
        Spacer(Modifier.width(16.dp))
        Image(
            painter = painterResource(id = R.drawable.checked),
            contentDescription = "checked",
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontFamily = pretendard,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF383838)
        )
    }
}