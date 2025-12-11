package com.example.gravit.main.Home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.MainPageResponse
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.error.NotFoundScreen
import com.example.gravit.main.Study.Lesson.PillShape
import com.example.gravit.main.Study.Lesson.RoundBox
import com.example.gravit.ui.theme.pretendard

@Composable
fun HomeScreen(
    navController: NavController,
    onSessionExpired: () -> Unit
) {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(factory = HomeVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { vm.load() }
    LaunchedEffect(ui) {
        if (navigated) return@LaunchedEffect

        when (ui) {
            HomeViewModel.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401")
            }
            HomeViewModel.UiState.NotFound -> {
                onSessionExpired()
            }
            HomeViewModel.UiState.Failed -> {
                onSessionExpired()
            }
            else -> Unit
        }
    }
    when (ui) {
        HomeViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeViewModel.UiState.Success -> {
            val data = (ui as HomeViewModel.UiState.Success).data
            HomeUI(
                home = data,
                navController = navController
            )
        }
        else -> Unit
    }
}

@Composable
fun HomeUI(
    home: MainPageResponse,
    navController: NavController
) {

    val config = LocalConfiguration.current
    val designWidth = 360f
    val designHeight = 740f

    val scaleW = config.screenWidthDp.toFloat() / designWidth
    val scaleH = config.screenHeightDp.toFloat() / designHeight

    fun dw(v: Float) = (v * scaleW).dp
    fun dh(v: Float) = (v * scaleH).dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_back),
            contentDescription = "main back",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dh(70f))
                    .padding(horizontal = dw(19f)),
                verticalArrangement = Arrangement.spacedBy(dh(10f), Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dh(48f)),
                    horizontalArrangement = Arrangement.spacedBy(dw(8f), Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gravit_main_logo),
                        contentDescription = "gravit typo",
                        modifier = Modifier
                            .width(dw(133f))
                            .height(dh(32f))
                    )
                }
            }

            Spacer(Modifier.height(dh(60f)))
            Row(
                modifier = Modifier
                    .width(dw(328f))
                    .height(dh(84f))
                    .padding(horizontal = dw(16f)),
                horizontalArrangement = Arrangement.spacedBy(dw(10f), Alignment.Start),
                verticalAlignment = Alignment.Bottom,
            ) {
                val nickname = home.nickname
                Text(
                    text = "어서오세요, \n${nickname}님!",
                    style = TextStyle(
                        fontSize = 28.sp,
                        lineHeight = 42.sp,
                        fontWeight = FontWeight(700),
                        fontFamily = pretendard,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .width(dw(206f))
                        .height(dh(84f))
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dw(16f))
            ) {
                Spacer(Modifier.height(dh(8f)))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dh(25f)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val level = home.userLevelDetail.level
                            val xp = home.userLevelDetail.xp
                            val league = home.leagueName
                            PillShape(
                                img = R.drawable.rank_cup,
                                league = league
                            )
                            Spacer(modifier = Modifier.width(dw(8f)))
                            PillShape(
                                img = R.drawable.xp_mark,
                                xp = xp.toString()
                            )

                            Spacer(modifier = Modifier.width(dw(8f)))
                            LevelGauge(lv = level, xp = xp, modifier = Modifier.height(dh(25f)))
                        }
                        Spacer(modifier = Modifier.height(dh(16f)))
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .height(dh(186f))
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(all = dh(16f))
                                            .fillMaxSize()
                                    ) {
                                        CustomText(
                                            text = "오늘의 미션🔥",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp,
                                            modifier = Modifier.size(
                                                dw(128f),
                                                dh(24f)
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(dh(12f)))

                                        Canvas(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(1.dp)
                                        ) {
                                            drawLine(
                                                color = Color(0xFFA8A8A8),
                                                start = Offset(0f, 0f),
                                                end = Offset(size.width, 0f),
                                                strokeWidth = 3f,
                                                pathEffect = PathEffect.dashPathEffect(
                                                    floatArrayOf(6f, 4f)
                                                ),
                                                cap = StrokeCap.Butt
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(dh(12f)))

                                        val mission = home.missionDetail.missionDescription
                                        val missionXp = home.missionDetail.awardXp
                                        val isCompleted = home.missionDetail.isCompleted

                                        if (!isCompleted) {
                                            Row {
                                                CustomText(
                                                    text = "•",
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF222124)
                                                )
                                                CustomText(
                                                    text = mission,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF222124)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(dh(3f)))

                                            CustomText(
                                                text = "완료시 ${missionXp}XP",
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = Color(0xFF494949),
                                                modifier = Modifier.padding(start = dw(8f))
                                            )

                                            Spacer(modifier = Modifier.weight(1f))

                                            Button(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(dh(39f)),
                                                onClick = {
                                                    val route =
                                                        if (mission == "새로운 친구 팔로우하기") {
                                                            "user"
                                                        } else {
                                                            "chapter"
                                                        }
                                                    navController.navigate(route) {
                                                        launchSingleTop = true
                                                    }
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFFF2F2F2),
                                                    contentColor = Color.Black
                                                ),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                CustomText(
                                                    text = "도전하러 가기",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp,
                                                    color = Color.Black,
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.mission_complete),
                                                    contentDescription = "mission completed",
                                                    modifier = Modifier.size(dh(92f))
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(dw(8f)))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RoundBox(
                                        title = "행성 정복률",
                                        value = "${home.learningDetail.planetConquestRate}%",
                                        img = R.drawable.rocket_main,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.height(dh(8f)))
                                    RoundBox(
                                        title = "연속 학습일",
                                        value = "${home.learningDetail.consecutiveSolvedDays}일",
                                        img = R.drawable.fire,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(dh(16f)))

                        val chapterId = home.learningDetail.recentSolvedChapterId
                        val chapterName = home.learningDetail.recentSolvedChapterTitle
                        val progressRate = home.learningDetail.recentSolvedChapterProgressRate
                        val rate = progressRate.toFloatOrNull() ?: 0f
                        val bgResId = previousImg[chapterId] ?: 0

                        PreviousButton(
                            chapterId = chapterId,
                            chapterName = chapterName,
                            progressRate = rate,
                            backgroundImg = bgResId,
                            onClick = {
                                if (chapterId == 0) {
                                    navController.navigate("chapter") {
                                        launchSingleTop = true
                                    }
                                } else {
                                    navController.navigate("unit/$chapterId") {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )

                        Spacer(Modifier.height(dh(12f)))
                    }
                }
            }
        }
    }
}

@Composable
fun CustomText (
    modifier: Modifier = Modifier,
    text: String?,
    fontFamily: FontFamily = pretendard,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    color: Color = Color.Black,
    shadow: Shadow? = null
) {
    if (text != null) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontSize = fontSize,
                shadow =  shadow
            ),
            color = color,
            modifier = modifier
        )
    }
}

