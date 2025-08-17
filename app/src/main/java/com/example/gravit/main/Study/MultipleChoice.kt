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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultipleChoice() {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Text(
                    text = "챕터 이름",
                    fontSize = 20.sp,
                    fontFamily = pretendard,
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
                        fontFamily = pretendard,
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
                        fontWeight = FontWeight.Bold,
                        fontFamily = pretendard
                    )
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
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFDCDCDC),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column {
                    repeat(5) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
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
                        fontFamily = pretendard,
                        textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "#### 학습출제가 중단됩니다.\n정말 학습을 그만두시나요?",
                        fontSize = 16.sp,
                        fontFamily = pretendard,
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
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8100B3),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text("계속하기",
                            fontSize = 16.sp,
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "그만두기",
                        color = Color(0xFF6D6D6D),
                        fontFamily = pretendard,
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

@Preview(showBackground = true)
@Composable
fun MultipleChoicePreview() {
    MultipleChoice()
}
