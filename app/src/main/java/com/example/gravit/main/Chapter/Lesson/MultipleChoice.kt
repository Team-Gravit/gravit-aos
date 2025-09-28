package com.example.gravit.main.Chapter.Lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.R
import com.example.gravit.api.OptionDto
import com.example.gravit.api.Problems
import com.example.gravit.ui.theme.pretendard
import kotlin.collections.mapIndexed
import kotlin.text.isNotBlank

@Composable
fun MultipleChoice(
    options: List<OptionDto>,
    problemNum: Int,
    selectedIndex: Int?,
    submitted: Boolean,
    onSelect: (Int?) -> Unit,
    onSubmit: (Int) -> Unit,
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
    val correctIdx = remember(displayOptions) {
        displayOptions.indexOfFirst { it.isAnswer }.takeIf { it >= 0 }
    }
    Box(modifier = modifier.fillMaxSize()) {
        val isMyAnswerCorrect = submitted && selectedIndex == correctIdx
        val useScroll = submitted && !isMyAnswerCorrect

        val columnModifier = if (useScroll) {
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
        } else {
            Modifier.fillMaxSize()
        }

        Column(columnModifier) {
            displayOptions.forEachIndexed { idx, opt ->
                val isSelected = selectedIndex == idx
                val enabled = !submitted && opt.text.isNotBlank()
                val isRight = submitted && idx == correctIdx
                val isWrong = submitted && isSelected && idx != correctIdx
                val explanationToShow = if (isWrong) opt.explanation else null

                OptionCell(
                    num = opt.badge,
                    answer = opt.text,
                    isSelected = isSelected,
                    isRight = isRight,
                    isWrong = isWrong,
                    enabled = enabled,
                    showEye = !submitted && selectedIndex == null,
                    explanation = if (useScroll) explanationToShow else null,
                    onClick = {
                        if (!enabled) return@OptionCell
                        onSelect(idx)
                        onSubmit(idx)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    problemNum = problemNum,
                    idx = idx,
                )
            }
        }

        val fabState = when {
            submitted -> FabState.NEXT
            else -> FabState.HIDDEN
        }

        if (fabState != FabState.HIDDEN) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 20.dp)
            ) {
                when (fabState) {
                    FabState.NEXT -> {
                        Image(
                            painter = painterResource(id = R.drawable.next_on),  // 다음
                            contentDescription = "다음",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    onNext()
                                }
                        )
                    }
                    else -> Unit
                }
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
    explanation: String?,
    problemNum: Int,
    idx: Int
) {
    var isShown by remember(problemNum, idx) { mutableStateOf(true) }
    val rowAlpha = if (showEye && !isSelected && !isShown) 0.4f else 1f
    val showExplanation = !explanation.isNullOrBlank()

    LaunchedEffect(showEye) {
        if (!showEye) isShown = true
    }

    Row(
        modifier = modifier
            .alpha(rowAlpha)
            .clickable(enabled = enabled && isShown) { onClick() }
            .background(if (isSelected) Color(0xFFDCDCDC) else Color(0xFFF2F2F2))
            .padding(horizontal = 16.dp, vertical = if (isSelected) 8.dp else 0.dp),
        verticalAlignment = if (showExplanation) Alignment.Top else Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .heightIn(min = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        when {
                            isRight -> Color(0xFF00A80B)
                            isWrong -> Color(0xFFFF0000)
                            else -> Color.White
                        }
                    )
                    .border(1.dp, Color(0xFF6D6D6D), RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        isWrong -> "X"
                        isRight -> "V"
                        else -> num
                    },
                    color = when {
                        isRight || isWrong -> Color.White
                        isSelected -> Color.Black
                        else -> Color(0xFF6D6D6D)
                    }
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = answer,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                fontSize = 16.sp,
                color = when {
                    isRight -> Color(0xFF00A80B)
                    isWrong -> Color(0xFFFF0000)
                    else -> Color(0xFF6D6D6D)
                }
            )

            if (showExplanation) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = explanation!!,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color(0xFFD00000),
                )
            }
        }
        if (showEye && !isSelected) {
            Icon(
                painter = painterResource(id = if (isShown) R.drawable.eye else R.drawable.close_eye),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(24.dp)
                    .clickable { isShown = !isShown }
            )
        }
    }
}