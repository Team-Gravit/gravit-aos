package com.example.gravit.main.Chapter.Lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.R
import com.example.gravit.api.Problems
import com.example.gravit.ui.theme.pretendard


typealias Problem = Problems
@Composable
fun MultipleChoice(
    problem: Problem,
    problemNum: Int,
    totalProblems: Int,
    selectedIndex: Int?,
    submitted: Boolean,
    onSelect: (Int) -> Unit,
    onSubmit: (String) -> Unit,
    isLast: Boolean,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    isCorrect: Boolean?
) {
    var showCompleteButton by remember(problemNum) { mutableStateOf(false) }
    var readyToSubmit by remember(problemNum) { mutableStateOf(false) }

    // 선택 해제되면 버튼 숨김
    LaunchedEffect(selectedIndex, submitted) {
        if (selectedIndex == null || submitted) {
            showCompleteButton = false
            readyToSubmit = false
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = modifier.background(Color(0xFFF2F2F2))) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                Column {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.clipboard),
                            contentDescription = "clipboard",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${problemNum}/${totalProblems}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = pretendard
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "빈칸에 알맞은 말을 고르세요",
                        fontSize = 16.sp,
                        fontFamily = pretendard
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
                    ){
                        Text(modifier = Modifier.padding(10.dp),
                            text = problem.question,
                            fontSize = 16.sp,
                            fontFamily = pretendard)
                    }
                }
            }
            Spacer(modifier=Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val mcOptions = remember(problem.options) { parseMCOptions(problem.options) }


                val displayOptions = remember(mcOptions) {
                    if (mcOptions.size >= 4) mcOptions.take(4)
                    else mcOptions + List(4 - mcOptions.size) { MCOption(badge = (mcOptions.size + it + 1).toString(), text = "") }
                }


                val correctIdx = remember(problem.answer) { correctIndexFromAnswer(problem.answer) }

                Column(Modifier.fillMaxSize()) {
                    displayOptions.forEachIndexed { idx, opt ->
                        val isSelected = selectedIndex == idx
                        val isRight    = submitted && idx == correctIdx
                        val isWrong    = submitted && isSelected && idx != correctIdx
                        val enabled    = !submitted && opt.text.isNotBlank()

                        OptionCell(
                            num = opt.badge,
                            answer = opt.text,
                            isSelected = isSelected,
                            isRight = isRight,
                            isWrong = isWrong,
                            enabled = enabled,
                            onClick = {
                                if (enabled) {
                                    onSelect(idx)
                                    showCompleteButton = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)          //4칸 동일 높이
                                .padding(vertical = 6.dp)
                        )
                    }
                }

            }
        }

        //우하단 체크 ↔ 다음
        val fabState = when {
            !submitted && selectedIndex != null -> FabState.SUBMIT
            submitted && !isLast -> FabState.NEXT
            else -> FabState.HIDDEN
        }

        if (fabState != FabState.HIDDEN) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 20.dp)
            ) {
                when (fabState) {
                    FabState.SUBMIT -> {
                        val selectedAnswerStr = ((selectedIndex ?: -1) + 1).toString()
                        Image(
                            painter = painterResource(id = R.drawable.check_on),
                            contentDescription = "채점",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { onSubmit(selectedAnswerStr) }
                        )
                    }
                    FabState.NEXT -> {
                        Image(
                            painter = painterResource(id = R.drawable.next_on),  // 다음
                            contentDescription = "다음",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { onNext() }
                        )
                    }
                    else -> Unit
                }
            }
        }

    }
}


data class MCOption(
    val badge: String,  // 동그라미 안에 들어갈 숫자
    val text: String    // 오른쪽에 표시할 텍스트
)

fun parseMCOptions(raw: String?): List<MCOption> {
    if (raw.isNullOrBlank()) return emptyList()

    val pieceRegex = Regex("^\\s*(\\d+)\\.?\\s*(.*)$") // "1. " / "1 " 제거
    return raw.split(";")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .mapIndexed { idx, piece ->
            val m = pieceRegex.find(piece)
            if (m != null) {
                val numStr = m.groupValues[1]             // "1"
                val label  = m.groupValues[2].trim()      // "행,열"
                MCOption(badge = numStr, text = label)
            } else {
                // 숫자 접두사가 없을 때도 안전하게
                MCOption(badge = (idx + 1).toString(), text = piece)
            }
        }
}
// 정답 인덱스
fun correctIndexFromAnswer(answer: String?): Int = (answer?.trim()?.toIntOrNull() ?: 1) - 1

@Composable
private fun OptionCell(
    num: String,
    answer: String,
    isSelected: Boolean,
    isRight: Boolean,
    isWrong: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .background(
                when {
                    isSelected -> Color(0xFFDCDCDC)
                    else -> Color(0xFFF2F2F2)
                }
            )
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(
                    when {
                        isRight -> Color(0xFF00A80B)
                        isWrong -> Color(0xFFFF0000)
                        isSelected -> Color.Black
                        else -> Color.White
                    }
                )
                .border(1.dp, Color(0xFF6D6D6D), RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center

        ){
            Text(
                text = when{
                    isWrong -> "X"
                    isRight -> "V"
                    else -> num
                },
                color = when {
                    isRight -> Color.White
                    isWrong -> Color.White
                    isSelected -> Color.White
                    else -> Color(0xFF6D6D6D)
                }
            )
        }
        Text(
            text = answer,
            fontWeight = FontWeight.Medium,
            fontFamily = pretendard,
            fontSize = 16.sp,
            color = Color(0xFF6D6D6D)
        )
    }
}