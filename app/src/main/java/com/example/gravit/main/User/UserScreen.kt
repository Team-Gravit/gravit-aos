package com.example.gravit.main.User

import android.R.attr.maxLines
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.BadgeCategoryResponses
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.pretendard
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import com.example.gravit.Responsive
import com.example.gravit.api.BadgeResponses

@Composable
fun UserScreen(
    navController: NavController,
    onSessionExpired: () -> Unit
) {

    val context = LocalContext.current
    val vm: UserScreenVM = viewModel(
        factory = UserVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }
    var navigated by remember { mutableStateOf(false) }
    when (ui) {
        UserScreenVM.UiState.SessionExpired -> {
            navigated = true
            navController.navigate("error/401") {
                popUpTo(0); launchSingleTop = true; restoreState = false
            }
        }
        UserScreenVM.UiState.NotFound -> {
            navigated = true
            navController.navigate("error/404") {
                popUpTo(0); launchSingleTop = true; restoreState = false
            }
        }
        UserScreenVM.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        UserScreenVM.UiState.Failed -> {
            navigated = true
            onSessionExpired()
        }
        else -> Unit
    }

    val s = (ui as? UserScreenVM.UiState.Success)?.data

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
                    color = Color(0xFF222222)
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lamp),
                        contentDescription = "notification",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                navController.navigate("user/notice")
                            }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Image(
                        painter = painterResource(id = R.drawable.setting),
                        contentDescription = "setting",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                navController.navigate("user/setting")
                            }
                    )
                }
            }

            HorizontalDivider(
                color = Color.Black.copy(alpha = 0.1f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(129.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(ProfilePalette.idToColor(s?.user?.profileImgNumber ?: 0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile_logo),
                            contentDescription = "profile logo",
                            modifier = Modifier.size(32.dp, 40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            s?.user?.nickname?.let {
                                Text(
                                    text = it,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = pretendard,
                                    color = Color(0xFF222222)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "@${s?.user?.handle}",
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp,
                                fontFamily = pretendard,
                                color = Color(0xFF222222),
                                modifier = Modifier.alpha(80f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = {
                                    navController.navigate("user/followList?tab=followers") {
                                        launchSingleTop = true   // 중복 쌓임 방지
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color(0xFF222222),
                                    containerColor = Color.White,
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                            ) {
                                Text(
                                    text = "팔로워  ${s?.user?.follower}",
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
                                onClick = {
                                    navController.navigate("user/followList?tab=following") {
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color(0xFF222222),
                                    containerColor = Color.White,
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
                            ) {
                                Text(
                                    text = "팔로잉  ${s?.user?.following}",
                                    fontFamily = pretendard,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF222222),
                                    modifier = Modifier
                                        .alpha(0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                navController.navigate("user/addfriend") {
                                    launchSingleTop = true   // 중복 쌓임 방지
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8100B3),
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF8100B3).copy(alpha = 0.1f)),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.group),
                                    contentDescription = "user",
                                    modifier = Modifier
                                        .padding(vertical = 3.dp)
                                        .size(30.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "친구추가",
                                    fontFamily = pretendard,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
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
            Spacer(Modifier.height(16.dp))
            Row (
                modifier = Modifier.padding(horizontal = 17.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "뱃지",
                    fontFamily = pretendard,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = Color(0xFF494949),
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("${s?.badges?.earnedCount}개")
                        }
                        append(" ")
                        withStyle(
                            SpanStyle(
                                color = Color(0xFF6D6D6D),
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("획득")
                        }
                    },
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontSize = 12.sp,
                    )
                )
            }
            val categories: List<BadgeCategoryResponses> =
                (s?.badges?.badgeCategoryResponses.orEmpty()).sortedBy { it.order }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 17.dp)
            ) {
                items(categories, key = { it.categoryId }) { category ->
                    Spacer(Modifier.height(20.dp))
                    BadgeCategorySection(category = category)
                    if(category.categoryId != categories.size){
                        Spacer(Modifier.height(20.dp))
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Responsive.h(1f))
                                .align(Alignment.CenterHorizontally)
                        ) {
                            drawLine(
                                color = Color(0xffC3C3C3),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 3f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)),
                                cap = StrokeCap.Butt
                            )
                        }
                    }else {
                        Spacer(Modifier.height(40.dp))
                    }

                }
            }

        }
    }
}

@Composable
private fun BadgeCategorySection(category: BadgeCategoryResponses) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.categoryName,
                fontFamily = pretendard,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF494949)
            )
            Spacer(Modifier.width(6.dp))
            if(category.categoryName == "풀이 속도"){
                Text(
                    text = "*85% 이상의 정답률만 인정해요.",
                    fontFamily = pretendard,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6D6D6D),
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }

        }
        Spacer(Modifier.height(8.dp))

        Surface(
            color = Color(0xFFF2F2F2),
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                maxItemsInEachRow = 5,
                horizontalArrangement = Arrangement.spacedBy(22.dp,Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                category.badgeResponses
                    .sortedBy { it.order }
                    .forEach { badge ->
                        BadgeChip(badge = badge)
                    }
            }
        }
    }
}

@Composable
private fun BadgeChip(badge: BadgeResponses) {

    val darkenFilter = ColorFilter.colorMatrix(
        ColorMatrix(
            floatArrayOf(
                0.45f, 0f, 0f, 0f, 0f,
                0f, 0.38f, 0f, 0f, 0f,
                0f, 0f, 0.34f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .wrapContentWidth()
    ) {
        Box(
            modifier = Modifier.size(42.dp, 64.dp),
            contentAlignment = Alignment.Center
        ) {
            val iconRes = R.drawable.badge
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                colorFilter = if (!badge.earned) darkenFilter else null
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = badge.name,
            fontFamily = pretendard,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF494949),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
