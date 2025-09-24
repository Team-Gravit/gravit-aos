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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.main.User.Setting.LogoutVMFactory
import com.example.gravit.main.User.Setting.LogoutViewModel
import com.example.gravit.ui.theme.pretendard


@Composable
fun Setting(
    navController: NavController,
    onLogout: () -> Unit){
    var isChecked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val vm: LogoutViewModel = viewModel(factory = LogoutVMFactory(context))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(Color.White)
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
                    .height(100.dp),
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
                            text = "계정 정보",
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xCC222222),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "내 정보",
                            style = TextStyle(
                                fontSize = 15.sp,
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
                                    navController.navigate("user/account?nickname={nickname}")
                                }
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
                    .background(Color.White)
                    .height(140.dp),
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
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xCC222222),
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
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = pretendard
                            ),
                            color = Color(0xFF222222),
                        )

                        Switch(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .scale(0.8f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFBA00FF),
                                checkedTrackColor = Color(0xFFEDD7FF),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFD9D9D9),
                                checkedBorderColor = Color.Transparent,
                                uncheckedBorderColor = Color.Transparent,
                                disabledCheckedBorderColor = Color.Transparent,
                                disabledUncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "개인정보 처리방침",
                            style = TextStyle(
                                fontSize = 15.sp,
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
                                    navController.navigate("user/privacypolicy")
                                }
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
                    .background(Color.White)
                    .height(100.dp),
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
                                fontSize = 15.sp,
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
                                    vm.logout { onLogout()}
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
                                fontSize = 15.sp,
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
                                    navController.navigate("user/screensetting")
                                }
                        )
                    }
                }
            }
        }
    }
}