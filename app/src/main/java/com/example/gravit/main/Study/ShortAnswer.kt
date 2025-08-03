package com.example.gravit.main.Study

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.R
import com.example.gravit.main.Study.Stage.Moon
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortAnswer() {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var answer by remember { mutableStateOf("") }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Text(
                    text = "챕터 이름",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                coroutineScope.launch {
                                    showSheet = true
                                    sheetState.show()
                                }
                            },
                        tint = Color(0xFF4D4D4D)
                    )

                    Text(
                        text = "스톱워치",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .weight(1f)
            ) {
                Column {
                    Text(
                        text = "문제 번호",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "다음 문장을 읽고, 빈칸에 들어갈 알맞은 말을 쓰시오.",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFDCDCDC),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AnswerInputField(
                    text = answer,
                    onTextChange = { answer = it }
                )
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch {
                        sheetState.hide()
                        showSheet = false
                    }
                },
                sheetState = sheetState,
                modifier = Modifier
                    .fillMaxHeight(0.9f)
            ) {
                Column(modifier = Modifier
                    .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.study_popup),
                        contentDescription = "학습 중단 팝업 일러",
                        modifier = Modifier.padding(20.dp)
                    )
                    Text("지금까지 푼 내역이\n모두 사라져요!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "#### 학습출제가 중단됩니다.\n정말 학습을 그만두시나요?",
                        fontSize = 16.sp,
                        color = Color(0xFF6D6D6D),
                        textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                                showSheet = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8100B3),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text("계속하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "그만두기",
                        color = Color(0xFF6D6D6D),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showSheet = false
                                }
                            },
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                    }
                }
            }
        }
    }

@Composable
fun NameInputFiled (
    text: String,
    onTextChange: (String) -> Unit,
) {
    val isValid = text.length >= 2
    val isError = text.isNotEmpty() && !isValid
    val showErrorMessage = isError
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            isError = isError,
            placeholder = {
                Text(
                    text = "닉네임",
                    color = Color(0xFF868686), // 회색
                    fontFamily = pretendard,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            textStyle = TextStyle(
                color = if (isError) Color.Red else Color.Black,
                fontFamily = pretendard,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .padding(start = screenWidth * (25f / 375f),
                    top = screenHeight * (12f / 815f))
                .size(width = screenWidth * (325f / 375f),
                    height = screenHeight * (50f / 815f)),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedIndicatorColor = if (isError) Color.Red else Color.Black,
                unfocusedIndicatorColor = if (isError) Color.Red else Color(0xFFC3C3C3),
                errorIndicatorColor = Color.Red,
                cursorColor = if (isError) Color.Red else Color.Black
            )
        )

        if (showErrorMessage) {
            Text(
                text = "이름은 두 글자 이상이어야 합니다",
                color = Color(0xFF868686),
                modifier = Modifier.padding(start = screenWidth * (25f / 375f),
                    top = screenHeight * (8f / 815f)),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            )
        }

    }
}

@Composable
fun AnswerInputField(
    text: String,
    onTextChange: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = "정답을 입력해주세요.",
                    color = Color(0xFF868686)
                )
            },
            textStyle = TextStyle(
                color =Color.Black,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color(0xFFC3C3C3),
                cursorColor = Color.Black
            ),
            maxLines = 5,//입력창에 보여지는 최대 줄 수
            minLines = 1,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShortAnswerPreview() {
    ShortAnswer()
}
