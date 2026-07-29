package com.inuappcenter.gravit.main.Study.Problem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.main.Study.Problem.ProblemViewModel
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.inuappcenter.gravit.api.OptionDto
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.AnswerResponse
import kotlinx.coroutines.delay
import kotlin.collections.mapIndexed
import kotlin.text.isNotBlank

@Composable
fun MultipleChoice(
    options: List<OptionDto>,
    problemNum: Long,
    selectedIndex: Int?,
    submitted: Boolean,
    onSelect: (Int?) -> Unit,
    onSubmit: (Int) -> Unit,
    isLast: Boolean,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    isCorrect: Boolean?,
    showRemoveFromWrongNote: Boolean = false,
    onRemoveFromWrongNote: () -> Unit = {},
    problemVm: ProblemViewModel,
) {
    var removeSnackBarText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(removeSnackBarText) {
        if (removeSnackBarText != null) {
            delay(1500)
            removeSnackBarText = null
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

    val removedFromWrongNote = problemVm.isRemovedFromWrongNote(problemNum)
    val isMyAnswerCorrect = submitted && selectedIndex == correctIdx
    val useScroll = submitted && !isMyAnswerCorrect

    val columnModifier = if (useScroll) {
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    } else {
        Modifier.fillMaxSize()
    }
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = columnModifier.padding(horizontal = 16.dp)) {
            if (submitted && isCorrect == true && showRemoveFromWrongNote && !removedFromWrongNote) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End
                ){
                    Text(
                        text = "오답노트에서 제외하기",
                        fontSize = 15.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFA8A8A8),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable {
                                problemVm.removeFromWrongNote(problemNum)
                                onRemoveFromWrongNote()
                                removeSnackBarText = "오답노트에서 제거되었아요."
                            }
                    )
                }
            }
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    problemNum = problemNum,
                    idx = idx,
                )
            }
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BlockButton(
                        text = "이전",
                        onClick = {onNext()},
                        state = ButtonState.Stroke,
                        style = AppTypography.Headline2,
                        modifier = Modifier.weight(1f)
                    )

                    BlockButton(
                        text = "다음",
                        onClick = {
                            selectedIndex?.let { index ->
                                onSubmit(index)
                            }
                        },
                        enabled = selectedIndex != null,
                        style = AppTypography.Headline2,
                        modifier = Modifier.weight(3f)
                    )
                }
            }
        }
        if(submitted && isCorrect!=null){
            Feedback(isCorrect = isCorrect, AnswerResponse(listOf("1", "2"), "설명"), onNext = onNext, isLast = isLast)
        }
        /* if (submitted || removeSnackBarText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.next_on),
                        contentDescription = "다음",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { onNext() }
                    )
                }

                if (removeSnackBarText != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        CustomSnackBar(removeSnackBarText!!)
                    }
                }
            }
        } */
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
    problemNum: Long,
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
            .fillMaxWidth()
            .heightIn(48.dp)
            .alpha(rowAlpha)
            .clickable(enabled = enabled && isShown) { onClick() }
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFDCDCDC) else AppColor.bg0)
            .padding(12.dp),
        verticalAlignment = if (showExplanation) Alignment.Top else Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .heightIn(min = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        when {
                            isRight -> Color(0xFF00A80B)
                            isWrong -> Color(0xFFFF0000)
                            else -> if (isSelected) Color(0xFFDCDCDC) else AppColor.bg0
                        }
                    )
                    .border(1.dp, AppColor.text3, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                if(isWrong){
                    Image(
                        painter = painterResource(id = R.drawable.xicon),
                        contentDescription = null
                    )
                }
                else if(isRight){
                    Image(
                        painter = painterResource(id = R.drawable.checkicon),
                        contentDescription = null
                    )
                } else{
                    Text(
                        text = num,
                        color= AppColor.text3,
                        style = AppTypography.Label2
                    )
                }

            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = answer,
                style = AppTypography.Label2,
                color = when {
                    isRight -> AppColor.successColor
                    isWrong -> AppColor.errorColor
                    else -> AppColor.text3
                }
            )

            if (showExplanation) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = explanation,
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
                    .clickable { isShown = !isShown },
                tint = Color(0xFF6D6D6D)
            )
        }
    }
}