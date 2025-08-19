package com.example.gravit.main.User

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.navigateToAccount
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.pretendard

@Composable
fun UserScreen(navController: NavController) {

    val context = LocalContext.current
    val vm: UserScreenVM = viewModel(
        factory = UserVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }
    when (ui) {
        UserScreenVM.UiState.SessionExpired -> {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        }

        else -> Unit
    }

    val s = ui as? UserScreenVM.UiState.Success?: return
    
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Text(
                    text = "사용자",
                    fontSize = 20.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center),
                )
                Image(
                    painter = painterResource(id = R.drawable.setting),
                    contentDescription = "setting",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.CenterEnd)
                        .clickable {
                            navController.navigate("setting")
                        }
                )
            }

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(screenHeight * (129f / 740f)),
                contentAlignment = Alignment.Center
            ) {
                Row (
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(screenWidth * (80f / 375f))
                            .clip(CircleShape)
                            .background(ProfilePalette.idToColor(s.data.profileImgNumber)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_logo),
                            contentDescription = "profile logo",
                            modifier = Modifier.size(
                                screenWidth * (32.34f / 375f),
                                screenHeight * (40.85f / 812f)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column (modifier = Modifier.weight(1f)){
                        Text(
                            text = "@${s.data.handle}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            fontFamily = pretendard,
                            color = Color(0xFF222222),
                            modifier = Modifier.alpha(80f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = s.data.nickname,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = pretendard,
                            color = Color(0xFF222222)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = { navController.navigate("followList?tab=followers") {
                                    launchSingleTop = true   // 중복 쌓임 방지
                                } },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color(0xFF222222),
                                    containerColor = Color.White,
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                            ) {
                                Text(
                                    text = "팔로워  ${s.data.follower}",
                                    fontFamily = pretendard,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF222222),
                                    modifier = Modifier
                                        .alpha(0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {navController.navigate("followList?tab=following") {
                                    launchSingleTop = true
                                }},
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color(0xFF222222),
                                    containerColor = Color.White,
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                            ) {
                                Text(
                                    text = "팔로잉  ${s.data.following}",
                                    fontFamily = pretendard,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF222222),
                                    modifier = Modifier
                                        .alpha(0.8f)
                                )
                            }
                        }
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
                    .height(130.dp),
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
                            text = "내 정보",
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "user",
                                modifier = Modifier
                                    .padding(vertical = 3.dp)
                                    .size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "계정 정보",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = pretendard
                                ),
                                color = Color(0xFF222222),
                            )
                        }

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    navController.navigateToAccount(s.data.nickname)
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.group),
                                contentDescription = "user",
                                modifier = Modifier
                                    .padding(vertical = 3.dp)
                                    .size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = "친구 추가",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = pretendard
                                ),
                                color = Color(0xFF222222),
                            )
                        }

                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.left_line),
                            contentDescription = "account info",
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    navController.navigate("addfriend")
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    // 프리뷰용 NavController 생성
    val navController = rememberNavController()
    UserScreen(navController = navController)
}