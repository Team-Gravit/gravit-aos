package com.example.gravit.main.Chapter.Lesson


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard

enum class FabState { HIDDEN, SUBMIT, NEXT }

@Composable
fun ShortAnswer(
    problem: Problem,
    totalProblems: Int,      // 총 문항 수
    submitted: Boolean,      // 제출 여부(부모 상태; 정오답 색상 표시용)
    modifier: Modifier = Modifier,
    problemNum: Int,
    onTextChange: (String) -> Unit,
    text: String,
    isCorrect: Boolean?,
    onSubmit: () -> Unit,
    isLast: Boolean,             //마지막 문제인지
    onNext: () -> Unit,          //다음 문제로
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var showCompleteButton by remember(problemNum) { mutableStateOf(false) }
    var readyToSubmit by remember(problemNum) { mutableStateOf(false) }

    // 입력을 비우면 상태 초기화
    LaunchedEffect(text) {
        if (text.isBlank()) {
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
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Row (verticalAlignment = Alignment.CenterVertically){
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
                        text = "다음 문장을 읽고, 빈칸에 들어갈 알맞은 말을 쓰시오.",
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
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = problem.question,
                            fontSize = 16.sp,
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                Column {
                    AnswerInputField(
                        value = text,
                        onValueChange = onTextChange,
                        submitted = submitted,
                        isCorrect = isCorrect,
                        onImeDone = {
                            if (text.isNotBlank() && !submitted) {
                                focusManager.clearFocus()
                                keyboard?.hide()
                                showCompleteButton = true                      //Done → 완료 버튼
                            }
                        }
                    )

                    if (!submitted && text.isNotBlank() && showCompleteButton && !readyToSubmit) {
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { readyToSubmit = true },  // 완료 → 체크
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF009FFF),
                                contentColor = Color.White
                            )
                        ) { Text("완료") }
                    }

                    if (submitted && isCorrect != null) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (isCorrect) "정답입니다!" else "정답: ",
                            color = if (isCorrect) Color(0xFF00A80B) else Color(0xFFD00000),
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

            }

        }
        val fabState = when {
            !submitted && readyToSubmit && text.isNotBlank() -> FabState.SUBMIT    // 채점
            submitted && !isLast                              -> FabState.NEXT      // 다음
            else                                              -> FabState.HIDDEN
        }

        if (fabState != FabState.HIDDEN) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 20.dp)
            ) {
                when (fabState) {
                    FabState.SUBMIT -> {
                        Image(
                            painter = painterResource(id = R.drawable.check_on),
                            contentDescription = "채점",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { if (!submitted) onSubmit() }
                        )
                    }
                    FabState.NEXT -> {
                        Image(
                            painter = painterResource(id = R.drawable.next_on),
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

@Composable
fun AnswerInputField(
    value: String,
    onValueChange: (String) -> Unit,
    submitted: Boolean,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier,
    placeholder: String = "정답을 입력해주세요.",
    onImeDone: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        var focused by remember { mutableStateOf(false) }
        val hasInput = value.isNotBlank()

        //라인색
        val indicator = when {
            submitted && isCorrect == true -> Color(0xFF00A80B) // 정답: 초록
            submitted && isCorrect == false -> Color(0xFFD00000) // 오답: 빨강
            hasInput || focused -> Color(0xFF5A5A5A)         // 입력 시작 라인 표시
            else -> Color(0xFFC3C3C3)                       // 아무 입력 전엔 라인 숨김
        }

        OutlinedTextField(
            value = value,
            onValueChange = { if (!submitted) onValueChange(it) },
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF868686),
                    fontFamily = pretendard
                )
            },
            textStyle = TextStyle(
                color =Color.Unspecified,
                fontSize = 18.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,

                focusedBorderColor = indicator,
                unfocusedBorderColor = indicator,
                disabledBorderColor = indicator,

                focusedTextColor = indicator,
                unfocusedTextColor = indicator,
                disabledTextColor = indicator,
                cursorColor = Color.Black
            ),
            maxLines = 5,
            minLines = 1,

            enabled = !submitted, //수정 불가
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onImeDone?.invoke() }),

        )
    }
}
