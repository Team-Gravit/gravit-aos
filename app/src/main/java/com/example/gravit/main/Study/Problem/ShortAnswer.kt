package com.inuappcenter.gravit.main.Study.Problem

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.main.Study.Problem.ProblemViewModel
import com.inuappcenter.gravit.api.AnswerResponse
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R
import kotlinx.coroutines.delay

enum class FabState { HIDDEN, SUBMIT, NEXT }

@Composable
fun ShortAnswer(
    submitted: Boolean,
    answer: AnswerResponse,
    problemId: Int,
    onTextChange: (String) -> Unit,
    text: String,
    isCorrect: Boolean?,
    onSubmit: () -> Unit,
    isLast: Boolean,
    onNext: () -> Unit,
    showRemoveFromWrongNote: Boolean = false,
    onRemoveFromWrongNote: () -> Unit = {},
    problemVm: ProblemViewModel

) {
    var removeSnackBarText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(removeSnackBarText) {
        if (removeSnackBarText != null) {
            delay(1500)
            removeSnackBarText = null
        }
    }
    val removedFromWrongNote = problemVm.isRemovedFromWrongNote(problemId)

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var inputFocused by remember(problemId) { mutableStateOf(false) }


    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                keyboard?.hide()
            })
        }
    ) {
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){
            AnswerInputField(
                value = text,
                onValueChange = onTextChange,
                submitted = submitted,
                isCorrect = isCorrect,
                focusRequester = focusRequester,
                onFocusChange = { inputFocused = it },
                onImeDone = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                }
            )

            if (submitted && isCorrect != null) {
                Spacer(Modifier.height(12.dp))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    FeedbackSubjective(isCorrect = isCorrect, answer = answer)
                    if (showRemoveFromWrongNote && isCorrect && !removedFromWrongNote) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "오답노트에서 제외하기",
                            fontSize = 15.sp,
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA8A8A8),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                problemVm.removeFromWrongNote(problemId)
                                onRemoveFromWrongNote()
                                removeSnackBarText = "오답노트에서 제거되었아요."
                            }
                        )
                    }
                }


            }

        }
        val canSubmit = text.isNotBlank()

        val fabState = when {
            submitted -> FabState.NEXT
            !submitted && !inputFocused -> FabState.SUBMIT
            else -> FabState.HIDDEN
        }

        if (fabState != FabState.HIDDEN) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    when (fabState) {
                        FabState.SUBMIT -> {
                            Image(
                                painter = painterResource(id = R.drawable.check_on),
                                contentDescription = "채점",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable(enabled = canSubmit) {
                                        focusManager.clearFocus()
                                        keyboard?.hide()
                                        onSubmit()
                                    }
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
                if (removeSnackBarText != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                    ) {
                        CustomSnackBar(removeSnackBarText!!)
                    }
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
        val answerText = answer.contents.joinToString(", ")

        Text(
            text = if (isCorrect) "정답입니다!" else "정답: $answerText",
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
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChange: (Boolean) -> Unit = {},
    onImeDone: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        var focused by remember { mutableStateOf(false) }
        val hasInput = value.isNotBlank()

        //라인색
        val indicator = when {
            submitted && isCorrect == true -> Color(0xFF00A80B)
            submitted && isCorrect == false -> Color(0xFFD00000)
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
                .focusRequester(focusRequester)
                .onFocusChanged {
                    focused = it.isFocused
                    onFocusChange(it.isFocused)
            },
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
            maxLines = 1,
            minLines = 1,

            enabled = !submitted,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onImeDone?.invoke() }),

        )
    }
}
