package com.inuappcenter.gravit.main.Home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.MainPageResponse
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.Units
import com.inuappcenter.gravit.main.Study.Chapter.resolvePlanetRes
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import com.inuappcenter.gravit.ui.theme.TierPalette
import com.inuappcenter.gravit.ui.theme.pretendard
import java.time.DayOfWeek
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
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
                navigated = true
                navController.navigate("error/404"){
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
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
                units = state.data.learningDetailResponse.units,
                navController = navController
            )
        }

        else -> Unit
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ConfigurationScreenWidthHeight", "DefaultLocale")
@Composable
fun HomeUI(
    home: MainPageResponse,
    units: List<Units>,
    navController: NavController
) {

    val config = LocalConfiguration.current
    val designWidth = 360f
    val designHeight = 740f

    val scaleW = config.screenWidthDp.toFloat() / designWidth
    val scaleH = config.screenHeightDp.toFloat() / designHeight

    fun dw(v: Float) = (v * scaleW).dp
    fun dh(v: Float) = (v * scaleH).dp

    val userLevelInfo = home.userLevelDetailResponse
    val userLeagueInfo = home.leagueDetailResponse
    val userLearningInfo = home.learningDetailResponse
    val recommendedInfo = home.recommendedUnitResponses[0]
    val weeklyInfo = home.weeklyLearningRecordResponse
    val missionInfo = home.missionDetailResponse

    val level = userLevelInfo.level
    val leagueName = userLeagueInfo.leagueName
    val levelRate = userLevelInfo.levelRate
    val levelProgress = (levelRate / 100f).coerceIn(0f, 1f)

    val leagueId = userLeagueInfo.leagueId
    val lpRange = (userLeagueInfo.maxLP - userLeagueInfo.minLP).toFloat()
    val leagueProgress = if (lpRange > 0f) {
        ((userLeagueInfo.currentLP - userLeagueInfo.minLP).toFloat() / lpRange)
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    val consecutiveDays = userLearningInfo.consecutiveSolvedDays

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F2))
            ){
                Image(
                    painter = painterResource(id = R.drawable.main_back),
                    contentDescription = "main back",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                Column(
                    modifier = Modifier
                        .padding(WindowInsets.statusBars.asPaddingValues())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
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
                                    progress = { levelProgress },
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
                                        .background(ProfilePalette.idToColor(home.profileImgNumber)),
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
                                text = "Lv ${level}",
                                style = AppTypography.Label1,
                                color = PrimitiveColor.Gray50
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { 1f },
                                    modifier = Modifier
                                        .size(36.dp),
                                    color = Color.White,
                                    strokeWidth = 2.2.dp
                                )

                                CircularProgressIndicator(
                                    progress = { leagueProgress },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .graphicsLayer {
                                            scaleX = -1f
                                        },
                                    color = Color(0xFFDD00FF),
                                    strokeWidth = 2.2.dp
                                )

                                Box(
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        painter = TierPalette.painterFor(leagueId),
                                        contentDescription = "tier",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .padding(1.5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = leagueName,
                                style = AppTypography.Label1,
                                color = PrimitiveColor.Gray50
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(90.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        val nickname = home.nickname

                        Text(
                            text = "어서오세요, ${nickname}님!",
                            style = AppTypography.Title3,
                            color = PrimitiveColor.Gray50
                        )

                        Text(
                            text = "그래빗과 함께 cs 지식을 마스터해요!",
                            style = AppTypography.Body1_Nomal,
                            color = PrimitiveColor.Gray100
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .defaultMinSize(minHeight = 124.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "연속 학습일",
                                        style = AppTypography.Label2,
                                        color = PrimitiveColor.Gray500
                                    )
                                    Text(
                                        text = "자세히 보기",
                                        style = AppTypography.Label2,
                                        textDecoration = TextDecoration.Underline,
                                        color = PrimitiveColor.Gray400,
                                        modifier = Modifier.clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ){
                                            navController.navigate("user")
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    buildAnnotatedString {
                                        withStyle(
                                            SpanStyle(
                                                color = Color.Black,
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (-0.6).sp
                                            )
                                        ) {
                                            append("$consecutiveDays ")
                                        }

                                        withStyle(
                                            SpanStyle(
                                                color = PrimitiveColor.Gray800,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        ) {
                                            append("일 연속")
                                        }
                                    },
                                    fontFamily = pretendard
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val currentDay = LocalDate.now().dayOfWeek
                                    val days = listOf(
                                        Triple("월", DayOfWeek.MONDAY, weeklyInfo.MONDAY),
                                        Triple("화", DayOfWeek.TUESDAY, weeklyInfo.TUESDAY),
                                        Triple("수", DayOfWeek.WEDNESDAY, weeklyInfo.WEDNESDAY),
                                        Triple("목", DayOfWeek.THURSDAY, weeklyInfo.THURSDAY),
                                        Triple("금", DayOfWeek.FRIDAY, weeklyInfo.FRIDAY),
                                        Triple("토", DayOfWeek.SATURDAY, weeklyInfo.SATURDAY),
                                        Triple("일", DayOfWeek.SUNDAY, weeklyInfo.SUNDAY)
                                    )
                                    days.forEach { (label, day, completed) ->
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (currentDay == day && completed) AppColor.Main1
                                                    else if (completed) Color(0xFFFBF1FF)
                                                    else Color.White
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (currentDay == day && completed) AppColor.Main1
                                                    else if (completed) AppColor.Main1
                                                    else PrimitiveColor.Gray400,
                                                    shape = RoundedCornerShape(4.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                style = AppTypography.Label1,
                                                color = if(currentDay == day && completed) PrimitiveColor.Gray50
                                                else if(completed) Color(0xFF8100B3)
                                                else PrimitiveColor.Gray500,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(156.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .clickable{
                                            val route =
                                                if (missionInfo.missionType == "FOLLOW_NEW_FRIEND") {
                                                    "user"
                                                } else {
                                                    "chapter"
                                                }
                                            navController.navigate(route) {
                                                launchSingleTop = true
                                            }
                                        }
                                        .padding(all = dh(16f))
                                ) {
                                    Column {
                                        Text(
                                            text = "오늘의 미션",
                                            style = AppTypography.Label2,
                                            color = PrimitiveColor.Gray500
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = missionInfo.missionDescription,
                                            style = AppTypography.Headline2,
                                            color = Color.Black
                                        )

                                        Spacer(modifier = Modifier.height(dh(4f)))

                                        Text(
                                            text = "완료 시 +${missionInfo.awardXp} XP",
                                            style = AppTypography.Caption1,
                                            color = AppColor.Main1
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "진행률",
                                                style = AppTypography.App_Caption2,
                                                color = PrimitiveColor.Gray500
                                            )
                                            Text(
                                                text = "${String.format("%.1f", missionInfo.progressRate)}%",
                                                style = AppTypography.App_Caption2,
                                                color = AppColor.Main1
                                            )
                                        }

                                        Spacer(Modifier.height(4.dp))

                                        RoundedGauge(
                                            height = dh(8f),
                                            width = 0.dp,
                                            rate = missionInfo.progressRate,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color(0xFFFBF1FF)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(dw(16f)))

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(156.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = resolvePlanetRes(recommendedInfo.chapterId)),
                                        contentDescription = null
                                    )
                                    Column(
                                        modifier = Modifier.padding(all = dh(16f))
                                    ){
                                        Text(
                                            text = "새 주제 시작하기",
                                            style = AppTypography.Label2,
                                            color = PrimitiveColor.Gray400
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = recommendedInfo.chapterTitle,
                                            style = AppTypography.Headline1,
                                            color = PrimitiveColor.Gray50
                                        )

                                        Spacer(modifier = Modifier.height(dh(3f)))

                                        Text(
                                            text = recommendedInfo.unitTitle,
                                            style = AppTypography.Caption1,
                                            color = PrimitiveColor.Gray400
                                        )
                                    }
                                    Column (
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .fillMaxWidth()
                                            .padding(all = dh(16f))
                                    ){
                                        Text(
                                            text = "학습하러 가기 ->",
                                            style = AppTypography.Label1,
                                            color = Color(0xFFFBF1FF),
                                            textDecoration = TextDecoration.Underline,
                                            modifier = Modifier.clickable(onClick = {
                                                navController.navigate("unit/${recommendedInfo.chapterId}")
                                            })
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(dh(16f)))

                    val chapterId = userLearningInfo.recentSolvedChapterId
                    val chapterName = userLearningInfo.recentSolvedChapterTitle
                    val progressRate = userLearningInfo.recentSolvedChapterProgressRate
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
                                "lessonList/${unit.unitId}/${unit.title}"
                            ) {
                                launchSingleTop = true
                            }
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}