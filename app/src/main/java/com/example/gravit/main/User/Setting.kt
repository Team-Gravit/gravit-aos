package com.example.gravit.main.User

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard


@Composable
fun Setting(navController: NavController){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(Color(0xFFDCDCDC))
    ){
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterStart)
                        .clickable {
                            navController.popBackStack()
                        },
                    tint = Color(0xFF4D4D4D)
                )
            }
            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .height(330.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "사용자 설정",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF4E4E4E),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "마케팅/ 이벤트 알림",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "화면 설정",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    navController.navigate("screensetting")
                                }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "공지사항",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    navController.navigate("notice")
                                }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "고객센터",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    navController.navigate("service")
                                }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "서비스 이용약관",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    navController.navigate("tos")
                                }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "계정정보 처리방침",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    navController.navigate("privacypolicy")
                                }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = "버전 정보",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = pretendard
                                ),
                                color = Color(0xFF222222),
                            )
                            Text(
                                text = "04.10.13",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = pretendard
                                ),
                                color = Color(0xFF4E4E4E),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    // 프리뷰용 NavController 생성
    val navController = rememberNavController()
    Setting(navController = navController)
}