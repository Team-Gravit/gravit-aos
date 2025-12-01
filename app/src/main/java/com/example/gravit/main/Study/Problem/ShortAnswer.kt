package com.example.gravit.main.Study.Problem


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.R
import com.example.gravit.api.AnswerResponse
import com.example.gravit.ui.theme.pretendard

enum class FabState { HIDDEN, SUBMIT, NEXT }

@Composable
fun ShortAnswer(
    submitted: Boolean,      // 제출 여부(부모 상태; 정오답 색상 표시용)
    answer: AnswerResponse,
    problemId: Int,
    onTextChange: (String) -> Unit,
    text: String,
    isCorrect: Boolean?,
    onSubmit: () -> Unit,
    isLast: Boolean,             //마지막 문제인지
    onNext: () -> Unit,          //다음 문제로
    showRemoveFromWrongNote: Boolean = false,
    onRemoveFromWrongNote: () -> Unit = {}
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var showCompleteButton by remember(problemId) { mutableStateOf(false) }
    var readyToSubmit by remember(problemId) { mutableStateOf(false) }

    // 입력을 비우면 상태 초기화
    LaunchedEffect(text) {
        if (text.isBlank()) {
            showCompleteButton = false
            readyToSubmit = false
        }
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){
            AnswerInputField(
                value = text,
                onValueChange = onTextChange,
                submitted = submitted,
                isCorrect = isCorrect,
                onImeDone = {
                    if (text.isNotBlank() && !submitted) {
                        focusManager.clearFocus()
                        keyboard?.hide()
                        showCompleteButton = true
                    }
                }
            )
            if (!submitted && text.isNotBlank() && showCompleteButton && !readyToSubmit) {
                readyToSubmit = true
            }
            if (submitted && isCorrect != null) {
                Spacer(Modifier.height(12.dp))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    FeedbackSubjective(isCorrect = isCorrect, answer = answer)
                    if (showRemoveFromWrongNote && isCorrect) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "오답노트에서 제외하기",
                            fontSize = 15.sp,
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA8A8A8),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onRemoveFromWrongNote() }
                        )
                    }
                }


            }

        }
        val fabState = when {
            !submitted && readyToSubmit && text.isNotBlank() -> FabState.SUBMIT    // 채점
            submitted -> FabState.NEXT      // 다음
            else -> FabState.HIDDEN
        }

        if (fabState != FabState.HIDDEN) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 20.dp)
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
fun FeedbackSubjective(
    isCorrect: Boolean,
    answer: AnswerResponse
) {
    Column{
        Text(
            text = if (isCorrect) "정답입니다!" else "정답: ${answer.content}",
            color = if (isCorrect) Color(0xFF00A80B) else Color(0xFFD00000),
            fontFamily = pretendard,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(10.dp))
        if (!isCorrect) {
            Text(
                text = answer.explanation,
                fontFamily = pretendard,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color(0xFFD00000)
            )
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
            hasInput || focused -> Color(0xFF5A5A5A)
            else -> Color(0xFFC3C3C3)
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
