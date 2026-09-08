package com.inuappcenter.gravit.main.Study.Problem

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
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gravit.main.Study.Problem.ProblemViewModel
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.api.OptionDto
import com.inuappcenter.gravit.R
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
    val removedFromWrongNote = problemVm.isRemovedFromWrongNote(problemNum)
    val correctAnswerText = remember(displayOptions) {
        displayOptions
            .firstOrNull { it.isAnswer }
            ?.text
            .orEmpty()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                displayOptions.forEachIndexed { idx, opt ->
                    val isSelected = selectedIndex == idx
                    val enabled = !submitted && opt.text.isNotBlank()
                    val isRight = submitted && opt.text.isNotBlank() && opt.isAnswer
                    val isWrong = submitted && opt.text.isNotBlank() && !opt.isAnswer

                    OptionCell(
                        num = opt.badge,
                        answer = opt.text,
                        isSelected = isSelected,
                        isRight = isRight,
                        isWrong = isWrong,
                        enabled = enabled,
                        showEye = !submitted,
                        onClick = {
                            if (!enabled) return@OptionCell
                            onSelect(idx)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        problemNum = problemNum,
                        idx = idx,
                        explanation = if (submitted) {
                            opt.explanation
                        } else {
                            null
                        },
                        isSubmitted = submitted,
                        showRemoveButton = submitted && isSelected && isCorrect == true && showRemoveFromWrongNote && !removedFromWrongNote,
                        onRemoveFromWrongNote = {
                            problemVm.removeFromWrongNote(problemNum)
                            onRemoveFromWrongNote()
                            removeSnackBarText = "오답노트에서 제거되었어요."
                        },
                        correctAnswerText = correctAnswerText,
                    )
                }
            }
        }
        if (removeSnackBarText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                CustomSnackBar(removeSnackBarText!!)
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
    problemNum: Long,
    idx: Int,
    isSubmitted: Boolean,
    showRemoveButton: Boolean,
    onRemoveFromWrongNote: () -> Unit,
    correctAnswerText: String,
) {
    var isOptionShown by remember(problemNum, idx) { mutableStateOf(true) }
    var isExpanded by remember(problemNum, idx) { mutableStateOf(false) }
    LaunchedEffect(problemNum, isSubmitted) {
        isExpanded = if (isSubmitted) {
            isSelected
        } else {
            false
        }
    }
    val rowAlpha = if (showEye && !isSelected && !isOptionShown) 0.4f else 1f
    val showExplanation = !explanation.isNullOrBlank()
    val showExpandedExplanation = isSubmitted && isExpanded && showExplanation

    val showResultStyle = isSubmitted && isExpanded
    val borderColor1 = when {
        showResultStyle && isRight -> AppColor.successColor
        showResultStyle && isWrong -> AppColor.errorColor
        !isSubmitted && isSelected -> AppColor.Main1
        else -> Color.Transparent
    }
    val borderColor2 = when {
        showResultStyle && isRight -> AppColor.successColor
        showResultStyle && isWrong -> AppColor.errorColor
        !isSubmitted && isSelected -> AppColor.Main1
        else -> AppColor.text3
    }
    val textColor = when {
        showResultStyle && isRight -> AppColor.successColor
        showResultStyle && isWrong -> AppColor.errorColor
        !isSubmitted && isSelected -> AppColor.bg0
        else -> AppColor.text3
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .alpha(rowAlpha)
            .clickable(
                enabled = isSubmitted || (enabled && isOptionShown)
            ) {
                if (isSubmitted) {
                    isExpanded = !isExpanded
                } else {
                    onClick()
                }
            }
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.bg0)
            .border(1.dp, borderColor1, RoundedCornerShape(8.dp))
            .padding(12.dp),
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .alignBy(FirstBaseline),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected && !isSubmitted) AppColor.Main1 else AppColor.bg0)
                        .border(1.dp, borderColor2, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = num,
                        color = textColor,
                        style = AppTypography.Label2
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = answer,
                style = AppTypography.Label2,
                color = when {
                    showResultStyle && isRight -> AppColor.successColor
                    showResultStyle && isWrong -> AppColor.errorColor
                    else -> AppColor.text3
                },
                modifier = Modifier
                    .weight(1f)
                    .alignByBaseline()
            )
            Spacer(Modifier.width(12.dp))
            if (showEye) {
                Icon(
                    painter = painterResource(id = if (isOptionShown) R.drawable.eye else R.drawable.close_eye),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isOptionShown = !isOptionShown
                        },
                    tint = Color(0xFF6D6D6D)
                )
            } else if (isSubmitted) {
                Icon(
                    painter = painterResource(id = if (isExpanded) R.drawable.chevron_up else R.drawable.chevron_down),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF6D6D6D)
                )
            }
        }
        if (showExpandedExplanation) {
            Spacer(Modifier.height(8.dp))
            if (isRight) {
                Text(
                    text = "👏🏻 정답입니다!",
                    color = AppColor.successColor,
                    style = AppTypography.Body1_Nomal
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "❌ 정답: ",
                        color = AppColor.errorColor,
                        style = AppTypography.Body1_Nomal
                    )

                    Text(
                        text = correctAnswerText,
                        color = AppColor.errorColor,
                        style = AppTypography.Body1_Nomal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColor.bg2)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = explanation,
                    style = AppTypography.Body2_Reading,
                    color = AppColor.text1
                )
            }
            if (showRemoveButton) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(width = 147.dp, height = 39.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg0)
                        .border(
                            width = 1.dp,
                            color = AppColor.errorColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onRemoveFromWrongNote()
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.book),
                        contentDescription = "오답노트 삭제",
                        modifier = Modifier.size(16.dp),
                        tint = AppColor.errorColor
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "오답노트 삭제",
                        style = AppTypography.Headline2,
                        color = AppColor.errorColor
                    )
                }
            }
        }
    }
}