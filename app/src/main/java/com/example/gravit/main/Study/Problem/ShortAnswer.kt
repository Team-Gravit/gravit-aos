package com.inuappcenter.gravit.main.Study.Problem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gravit.main.Study.Problem.ProblemViewModel
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.example.gravit.ui.theme.InlineButton
import com.example.gravit.ui.theme.InlineButtonIcon
import com.example.gravit.ui.theme.InlineButtonState
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.AnswerResponse
import com.inuappcenter.gravit.ui.theme.pretendard
import kotlinx.coroutines.delay

@Composable
fun ShortAnswer(
    submitted: Boolean,
    answer: AnswerResponse,
    problemId: Long,
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
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ){
            AnswerInputField(
                value = text,
                onValueChange = onTextChange,
                submitted = submitted,
                focusRequester = focusRequester,
                onFocusChange = { inputFocused = it },
                onImeDone = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                }
            )
            Spacer(Modifier.height(16.dp))
            InlineButton(
                text = "풀이보기",
                onClick = {},
                state = InlineButtonState.Stroke_Color,
                icon = InlineButtonIcon.L,
                style = AppTypography.Label2,
                color = AppColor.CTA,
                iconAsset = R.drawable.book,
                iconColor= AppColor.icon_color,
                modifier = Modifier
                    .size(97.dp, 32.dp)
                    .align(Alignment.End)
            )

            if (submitted && isCorrect != null) {
                Spacer(Modifier.height(12.dp))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Feedback(isCorrect = isCorrect, answer = answer, onNext = onNext, isLast = isLast)
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
            val canSubmit = text.isNotBlank()
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
                            focusManager.clearFocus()
                            keyboard?.hide()
                            onSubmit()
                        },
                        enabled = canSubmit,
                        style = AppTypography.Headline2,
                        modifier = Modifier.weight(3f)
                    )
                }
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

@Composable
fun Feedback(
    isCorrect: Boolean,
    answer: AnswerResponse,
    onNext: () -> Unit,
    isLast: Boolean
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(283.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AppColor.bg0)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Center

            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 16.dp)
                ) {
                    Text(
                        text = if (isCorrect) "👏🏻 정답입니다!" else "❌ 오답입니다!",
                        color = if (isCorrect) AppColor.successColor else AppColor.errorColor,
                        style = AppTypography.Headline1
                    )
                }
                val answerText = answer.contents.joinToString(", ")
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AppColor.bg2)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text(
                            text = "정답: $answerText",
                            style = AppTypography.Body2_Reading,
                            color = AppColor.text1
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = answer.explanation,
                            style = AppTypography.Body2_Reading,
                            color = AppColor.text1
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                BlockButton(
                    text = if (isLast) "결과보기(임시)" else "다음 문제 풀기",
                    onClick = onNext,
                )

            }
        }
    }
}

@Composable
fun AnswerInputField(
    value: String,
    onValueChange: (String) -> Unit,
    submitted: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "정답을 입력해주세요.",
    focusRequester: FocusRequester = FocusRequester(),
    onFocusChange: (Boolean) -> Unit = {},
    onImeDone: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {

        var focused by remember { mutableStateOf(false) }
        val hasInput = value.isNotBlank()

        //라인색
        val indicator = when {
            hasInput || focused -> AppColor.text1
            else -> AppColor.divider1
        }

        val color = when {
            hasInput || focused -> AppColor.text1
            else -> AppColor.text4
        }

        OutlinedTextField(
            value = value,
            onValueChange = { if (!submitted) onValueChange(it) },
            placeholder = {
                Text(
                    text = placeholder,
                    color = AppColor.text4,
                    style = AppTypography.Body2_Reading
                )
            },
            textStyle = AppTypography.Body2_Reading.copy(color =Color.Unspecified),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    focused = it.isFocused
                    onFocusChange(it.isFocused)
                },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AppColor.bg0,
                unfocusedContainerColor = AppColor.bg0,
                disabledContainerColor = AppColor.bg0,

                focusedBorderColor = indicator,
                unfocusedBorderColor = indicator,
                disabledBorderColor = indicator,

                focusedTextColor = color,
                unfocusedTextColor = color,
                disabledTextColor = color,
                cursorColor = AppColor.text1
            ),
            maxLines = 1,
            minLines = 1,

            enabled = !submitted,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onImeDone?.invoke() }),

        )
    }
}
