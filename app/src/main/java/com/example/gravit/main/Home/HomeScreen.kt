package com.inuappcenter.gravit.main.Home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.MainPageResponse
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.UnitDetailResponses
import com.inuappcenter.gravit.main.Study.Chapter.resolvePlanetRes
import com.inuappcenter.gravit.main.User.UserScreenVM
import com.inuappcenter.gravit.main.User.UserVMFactory
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import com.inuappcenter.gravit.ui.theme.TierPalette
import com.inuappcenter.gravit.ui.theme.mbc1961
import com.inuappcenter.gravit.ui.theme.pretendard

@Composable
fun HomeScreen(
    navController: NavController,
    onSessionExpired: () -> Unit
) {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(factory = HomeVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.load()
    }

    LaunchedEffect(ui) {
        if (navigated) return@LaunchedEffect

        when (ui) {
            HomeViewModel.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is HomeViewModel.UiState.Success -> {
            val state = ui as HomeViewModel.UiState.Success

            HomeUI(
                home = state.data,
                units = state.units,
                navController = navController
            )
        }

        else -> Unit
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomeUI(
    home: MainPageResponse,
    units: List<UnitDetailResponses>,
    navController: NavController
) {
    val config = LocalConfiguration.current
    val designWidth = 360f
    val designHeight = 740f

    val scaleW = config.screenWidthDp.toFloat() / designWidth
    val scaleH = config.screenHeightDp.toFloat() / designHeight

    fun dw(v: Float) = (v * scaleW).dp
    fun dh(v: Float) = (v * scaleH).dp

    val context = LocalContext.current
    val vm: UserScreenVM = viewModel(
        factory = UserVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

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

        val level = home.userLevelDetailResponse.level
        val league = home.leagueDetailResponse.leagueName

        val xpProgress =
            (home.userLevelDetailResponse.levelRate / 100f).coerceIn(0f, 1f)

        val currentLP = home.leagueDetailResponse.currentLP
        val maxLP = home.leagueDetailResponse.maxLP
        val lpProgress =
            if (maxLP > 0) {
                (currentLP.toFloat() / maxLP.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

        val s = (ui as? UserScreenVM.UiState.Success)?.data
        val tierId = tierIdFromKoreanName(home.leagueDetailResponse.leagueName)
        val consecutiveDays = home.learningDetailResponse.consecutiveSolvedDays

        val weeklyRecords = listOf(
            "월" to home.weeklyLearningRecordResponse.MONDAY,
            "화" to home.weeklyLearningRecordResponse.TUESDAY,
            "수" to home.weeklyLearningRecordResponse.WEDNESDAY,
            "목" to home.weeklyLearningRecordResponse.THURSDAY,
            "금" to home.weeklyLearningRecordResponse.FRIDAY,
            "토" to home.weeklyLearningRecordResponse.SATURDAY,
            "일" to home.weeklyLearningRecordResponse.SUNDAY
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dw(10f)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.size(36.dp),
                                color = Color.White,
                                strokeWidth = 2.2.dp
                            )

                            CircularProgressIndicator(
                                progress = { xpProgress },
                                modifier = Modifier
                                    .size(36.dp)
                                    .graphicsLayer {
                                        scaleX = -1f
                                    },
                                color = Color(0xFFDD00FF),
                                strokeWidth = 2.2.dp
                            )

                            Box(
                                modifier = Modifier
                                    .padding(1.5.dp)
                                    .size(25.dp)
                                    .clip(CircleShape)
                                    .background(
                                        ProfilePalette.idToColor(
                                            s?.user?.profileImgNumber ?: home.profileImgNumber
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_logo),
                                    contentDescription = "profile logo",
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Lv.${level}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(400),
                                fontFamily = pretendard,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { 1f },
                                modifier = Modifier.size(36.dp),
                                color = Color.White,
                                strokeWidth = 2.2.dp
                            )

                            CircularProgressIndicator(
                                progress = { lpProgress },
                                modifier = Modifier
                                    .size(40.dp)
                                    .graphicsLayer {
                                        scaleX = -1f
                                    },
                                color = Color(0xFFDD00FF),
                                strokeWidth = 2.2.dp
                            )

                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = TierPalette.painterFor(tierId),
                                    contentDescription = "tier",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(1.5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = league,
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight(400),
                                fontFamily = pretendard,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .width(dw(328f))
                        .height(dh(84f))
                        .padding(horizontal = dw(16f)),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    val nickname = home.nickname

                    Text(
                        text = "어서오세요, ${nickname}님!",
                        style = TextStyle(
                            fontSize = 28.sp,
                            lineHeight = 42.sp,
                            fontWeight = FontWeight(700),
                            fontFamily = pretendard,
                            color = Color.White
                        )
                    )

                    Text(
                        text = "그래빗과 함께 cs 지식을 마스터해요!",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight(400),
                            fontFamily = pretendard,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(dh(16f)))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dw(16f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = dh(16f), horizontal = dw(16f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "연속 학습일",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight(400),
                                        fontFamily = pretendard,
                                        color = Color(0xFFA8A8A8)
                                    )
                                )

                                Text(
                                    text = "자세히 보기",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight(400),
                                        fontFamily = pretendard,
                                        color = Color(0xFFA8A8A8),
                                        textDecoration = TextDecoration.Underline
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            color = Color.Black,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight(700)
                                        )
                                    ) {
                                        append("${consecutiveDays} ")
                                    }

                                    withStyle(
                                        SpanStyle(
                                            color = Color.Black,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight(500)
                                        )
                                    ) {
                                        append("일 연속")
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                weeklyRecords.forEach { (day, isSolved) ->
                                    val backgroundColor =
                                        if (isSolved) Color(0xFFFBF1FF) else Color.White

                                    val borderColor =
                                        if (isSolved) Color(0xFFBA00FF) else Color(0xFFC6C6C6)

                                    val textColor =
                                        if (isSolved) Color(0xFF8100B3) else Color(0xFFC6C6C6)

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(backgroundColor)
                                            .border(
                                                width = 1.dp,
                                                color = borderColor,
                                                shape = RoundedCornerShape(4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day,
                                            fontSize = 14.sp,
                                            fontFamily = pretendard,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .height(210.dp)
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
                                                text = "오늘의 미션",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                color = Color(0xFFA8A8A8)
                                            )

                                            Spacer(modifier = Modifier.height(5.dp))

                                            val mission = home.missionDetailResponse.missionDescription
                                            val missionXp = home.missionDetailResponse.awardXp
                                            val isCompleted = home.missionDetailResponse.isCompleted
                                            val missionProgress = home.missionDetailResponse.progressRate.coerceIn(0f, 100f)

                                            if (!isCompleted) {
                                                CustomText(
                                                    text = mission,
                                                    fontWeight = FontWeight(700),
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF222124)
                                                )

                                                Spacer(modifier = Modifier.height(3.dp))

                                                CustomText(
                                                    text = "완료시 +${missionXp}XP",
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFFBA00FF)
                                                )

                                                Spacer(modifier = Modifier.height(10.dp))

                                                Column {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = "진행률",
                                                            fontSize = 12.sp,
                                                            color = Color(0xFFA8A8A8)
                                                        )

                                                        Text(
                                                            text = "${missionProgress.toInt()}%",
                                                            fontSize = 12.sp,
                                                            color = Color(0xFFBA00FF)
                                                        )
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(7.dp)
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(0xFFFBF1FF))
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth(
                                                                    missionProgress / 100f
                                                                )
                                                                .height(7.dp)
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(Color(0xFFBA00FF))
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

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
                                                    shape = RoundedCornerShape(4.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF8100B3),
                                                        contentColor = Color.Black
                                                    ),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    CustomText(
                                                        text = "도전하러 가기",
                                                        fontWeight = FontWeight(500),
                                                        fontSize = 14.sp,
                                                        color = Color.White
                                                    )
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Image(
                                                        painter = painterResource(
                                                            id = R.drawable.mission_complete
                                                        ),
                                                        contentDescription = "mission completed",
                                                        modifier = Modifier.size(dh(92f))
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(15.dp))

                                    val recommendedUnit = home.recommendedUnitResponses.firstOrNull()
                                    val recommendedChapterId = recommendedUnit?.chapterId
                                    val recommendedBackground = resolvePlanetRes(recommendedChapterId ?: 2)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable(enabled = recommendedUnit != null) {
                                                recommendedUnit?.let { unit ->
                                                    navController.navigate(
                                                        "unit/${unit.chapterId}"
                                                    ) {
                                                        launchSingleTop = true
                                                    }
                                                }
                                            }
                                    ) {
                                        Image(
                                            painter = painterResource(id = recommendedBackground),
                                            contentDescription = "recommended chapter background",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        RoundedGauge(
                                            height = dh(8f),
                                            width = 0.dp,
                                            rate = missionInfo.progressRate,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color(0xFFFBF1FF)
                                        )

                                        Column(
                                            modifier = Modifier
                                                .padding(all = dh(16f))
                                                .fillMaxSize()
                                        ) {
                                            CustomText(
                                                text = "새 주제 시작하기",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )

                                            Spacer(modifier = Modifier.height(5.dp))

                                            CustomText(
                                                text = recommendedUnit?.chapterTitle ?: "챕터 이름",
                                                fontWeight = FontWeight(700),
                                                fontSize = 16.sp,
                                                color = Color.White,
                                                fontFamily = mbc1961
                                            )

                                            Spacer(modifier = Modifier.height(3.dp))

                                            CustomText(
                                                text = recommendedUnit?.unitTitle ?: "레슨 이름",
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = Color.White.copy(alpha = 0.75f)
                                            )

                                            Spacer(modifier = Modifier.weight(1f))

                                            Text(
                                                text = "학습하러 가기 →",
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp,
                                                color = Color.White,
                                                fontFamily = pretendard,
                                                textDecoration = TextDecoration.Underline
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            val chapterId =
                                home.learningDetailResponse.recentSolvedChapterId
                            val chapterName =
                                home.learningDetailResponse.recentSolvedChapterTitle
                            val progressRate =
                                home.learningDetailResponse.recentSolvedChapterProgressRate
                            val rate = progressRate.toFloat()

                            PreviousButton(
                                chapterId = chapterId,
                                chapterName = chapterName,
                                progressRate = rate,
                                units = units,
                                onClick = {
                                    if (chapterId == 0) {
                                        navController.navigate("chapter") {
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                onViewAllClick = {
                                    navController.navigate("unit/$chapterId") {
                                        launchSingleTop = true
                                    }
                                },
                                onUnitClick = { unit ->
                                    navController.navigate(
                                        "lessonList/${unit.unitSummaryResponse.unitId}/${unit.unitSummaryResponse.title}"
                                    ) {
                                        launchSingleTop = true
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomText(
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
                shadow = shadow
            ),
            color = color,
            modifier = modifier
        )
    }
}

private fun tierIdFromKoreanName(name: String?): Int = when (name) {
    "브론즈 3" -> 1
    "브론즈 2" -> 2
    "브론즈 1" -> 3

    "실버 3" -> 4
    "실버 2" -> 5
    "실버 1" -> 6

    "골드 3" -> 7
    "골드 2" -> 8
    "골드 1" -> 9

    "플래티넘 3" -> 10
    "플래티넘 2" -> 11
    "플래티넘 1" -> 12

    "다이아몬드 3" -> 13
    "다이아몬드 2" -> 14
    "다이아몬드 1" -> 15

    else -> TierPalette.DEFAULT_ID
}