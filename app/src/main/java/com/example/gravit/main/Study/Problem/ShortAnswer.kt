package com.inuappcenter.gravit.main.Study.Problem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.gravit.main.Study.Problem.ProblemViewModel
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.AnswerResponse
import kotlinx.coroutines.delay

@Composable
fun ShortAnswer(
    submitted: Boolean,
    answer: AnswerResponse,
    problemId: Long,
    onTextChange: (String) -> Unit,
    text: String,
    isCorrect: Boolean?,
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
        Column (modifier = Modifier.fillMaxSize()){
            Column (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
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
                    },
                    isCorrect = isCorrect
                )
                Spacer(Modifier.height(16.dp))
                if (submitted && isCorrect != null) {
                    Text(
                        text = if (isCorrect) "👏🏻 정답입니다!" else "❌ 정답: ${answer.contents.joinToString(", ")}",
                        color = if (isCorrect) AppColor.successColor else AppColor.errorColor,
                        style = AppTypography.Body1_Nomal
                    )
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
                            text = answer.explanation,
                            style = AppTypography.Body2_Reading,
                            color = AppColor.text1
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    if (showRemoveFromWrongNote && isCorrect && !removedFromWrongNote) {
                        Row(
                            modifier = Modifier
                                .size(147.dp, 39.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, AppColor.errorColor, RoundedCornerShape(8.dp))
                                .background(AppColor.bg0)
                                .align(Alignment.End)
                                .clickable{
                                    problemVm.removeFromWrongNote(problemId)
                                    onRemoveFromWrongNote()
                                    removeSnackBarText = "오답노트에서 제거되었아요."
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                painter = painterResource(id = R.drawable.book),
                                contentDescription = "개념노트",
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
        if (removeSnackBarText != null) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                CustomSnackBar(removeSnackBarText!!)
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
    onImeDone: (() -> Unit)? = null,
    isCorrect: Boolean?
) {
    Column(modifier = modifier.fillMaxWidth()) {

        var focused by remember { mutableStateOf(false) }
        val hasInput = value.isNotBlank()

        //라인색
        val indicator = when {
            submitted && isCorrect == true -> AppColor.successColor
            submitted && isCorrect == false -> AppColor.errorColor
            hasInput || focused -> AppColor.text1
            else -> AppColor.divider1
        }

        val color = when {
            submitted && isCorrect == true -> AppColor.successColor
            submitted && isCorrect == false -> AppColor.errorColor
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
