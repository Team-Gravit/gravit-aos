package com.example.gravit.main.Chapter.Lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gravit.ui.theme.pretendard

@Composable
fun StudyComplete(){
    Box(modifier = Modifier.fillMaxSize()
        .background(Color(0xFFF2F2F2))) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White)
            ) {
                Text(
                    text = "챕터 이름",
                    fontSize = 20.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .clickable {
                        },
                    tint = Color(0xFF4D4D4D)
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
                ){
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically){
                    Box(modifier = Modifier

                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCDCDC),
                            shape = RoundedCornerShape(16.dp)
                        ))
                    Box(modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCDCDC),
                            shape = RoundedCornerShape(16.dp)
                        ))
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(5f)){
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        top = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFDCDCDC),
                        shape = RoundedCornerShape(16.dp)
                    )
                )
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(2f)){
                Row(modifier = Modifier
                    .padding(start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 16.dp)
                    .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp)
                                )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCDCDC),
                            shape = RoundedCornerShape(16.dp)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCDCDC),
                            shape = RoundedCornerShape(16.dp)
                        )
                    )
                }
            }

            Button(
                onClick = {
                },
                modifier = Modifier
                    .padding(16.dp)
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudyCompletePreview() {
    StudyComplete()
}
