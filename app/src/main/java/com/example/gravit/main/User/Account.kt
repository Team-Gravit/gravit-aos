package com.example.gravit.main.User

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard

@Composable
fun Account(
    navController: NavController,
    nickname: String,
    onLogout: () -> Unit
){
    val context = LocalContext.current
    val vm: LogoutViewModel = viewModel(factory = LogoutVMFactory(context))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDCDCDC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
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
                    .align (Alignment.CenterStart)
                    .clickable { navController.popBackStack() },
                tint = Color(0xFF4D4D4D) ) }

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White)
            ) {
                Column (
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
                            text = "계정 정보",
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
                        Column {
                            Text(
                                text = "내 이름",
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = pretendard
                                ),
                                color = Color(0xFF4E4E4E),)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = nickname,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = pretendard
                                ),
                                color = Color(0xFF222222),)
                        }

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable { }
                        )
                    }
                }
            }
            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White)
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
                            text = "로그아웃",
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
                                    vm.logout { onLogout() }
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
                            text = "탈퇴하기",
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
                                    //탈퇴
                                }
                        )
                    }
                }
            }
        }
    }
}
