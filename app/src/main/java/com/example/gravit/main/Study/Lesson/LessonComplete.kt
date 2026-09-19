package com.inuappcenter.gravit.main.Study.Lesson

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.inuappcenter.gravit.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.api.LessonSubmissionSaveRequest
import com.inuappcenter.gravit.api.ProblemSubmissionSaveRequests
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Home.RoundedGauge
import com.inuappcenter.gravit.main.Home.calculateXpProgress
import com.inuappcenter.gravit.main.Study.Problem.FormatSeconds
import com.inuappcenter.gravit.main.Study.Problem.LessonVMFactory
import com.inuappcenter.gravit.main.Study.Problem.LessonViewModel
import com.inuappcenter.gravit.main.User.RankInfo
import com.inuappcenter.gravit.ui.theme.TierPalette.painterFor
import com.inuappcenter.gravit.ui.theme.pretendard

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun LessonComplete(
    navController: NavController,
    accuracy: Int,
    learningTime: Int,
    lessonId: Long,
    chapterId: Long
){
    val homeEntry = navController.getBackStackEntry("home")
    val problemList = homeEntry.savedStateHandle.get<ArrayList<ProblemSubmissionSaveRequests>>("problemList")
    val lessonSubmission = LessonSubmissionSaveRequest(lessonId, learningTime, accuracy)
    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(factory = LessonVMFactory(RetrofitInstance.api, context))

    LaunchedEffect(Unit) {
        vm.submitResults(lessonSubmission, problemList)
        homeEntry.savedStateHandle.remove<ArrayList<ProblemSubmissionSaveRequests>>("problemList")
    }

    val submit by vm.submit.collectAsState()

    LaunchedEffect(submit) {
        when (submit) {
            LessonViewModel.SubmitState.SessionExpired ->  {
                navController.navigate("error/401") {
                    popUpTo(navController.currentBackStackEntry?.destination?.id ?: return@navigate) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            LessonViewModel.SubmitState.NotFound -> {
                navController.navigate("error/404") {
                    popUpTo(navController.currentBackStackEntry?.destination?.id ?: return@navigate) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }
    val s = (submit as? LessonViewModel.SubmitState.Success)
    val leagueName = s?.data?.leagueName
    val userLevelResponse = s?.data?.userLevelResponse
    val unitSummary = s?.data?.unitSummaryResponse
    val isLevelUp = s?.isLevelUp?: false
    val isLeaguePromoted = s?.isLeaguePromoted?: false

    var popupType by rememberSaveable { mutableStateOf<String?>(null) }
    LaunchedEffect(s) {
        popupType = when {
            isLevelUp -> "level"
            isLeaguePromoted -> "league"
            else -> null
        }
    }
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = false
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.results_back),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(WindowInsets.statusBars.asPaddingValues())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(75.dp))
                    Text(
                        text = "${planetName[chapterId]} 정복에 더 가까워졌어요!",
                        style = AppTypography.Heading1,
                        color = AppColor.text1w
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${unitSummary?.title?: ""} 학습 완료",
                        style = AppTypography.Label2,
                        color = AppColor.text2w
                    )
                    Spacer(Modifier.height(40.dp))

                    val painter = painterResource(id = converterInt(chapterId))

                    Box(
                        modifier = Modifier
                            .padding(end = 22.dp)
                            .align(Alignment.End)
                            .size(113.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.size(113.dp),
                            colorFilter = ColorFilter.tint(
                                color = AppColor.bg0.copy(alpha = 0.2f),
                                blendMode = BlendMode.SrcIn
                            )
                        )
                        Image(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.size(107.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Column (modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                    ){
                        Row (modifier = Modifier.fillMaxWidth()){
                            Text(
                                text = "${userLevelResponse?.xp}XP",
                                style = AppTypography.Label1,
                                color = AppColor.text3
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = "LV${userLevelResponse?.nextLevel?: 2}까지",
                                style = AppTypography.Label1,
                                color = AppColor.Main1
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        val xp = userLevelResponse?.xp ?: 0
                        val progress = calculateXpProgress(xp)
                        RoundedGauge(
                            rate = progress,
                            height = 8.dp,
                            width = 0.dp,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFBF1FF)
                        )
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColor.bg0),
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                val resultsInfo = listOf(
                                    "$accuracy%",
                                    "정답률",
                                    FormatSeconds(learningTime),
                                    "풀이시간"
                                ).chunked(2)
                                val isLeague = false
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp)
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    resultsInfo.forEachIndexed { index, item ->
                                        RankInfo(
                                            value = item[0],
                                            label = item[1],
                                            modifier = Modifier.weight(1f),
                                            color = if (index == 0 && isLeague) AppColor.Main1 else AppColor.text1
                                        )
                                        if (index != resultsInfo.lastIndex) {

                                            VerticalDivider(
                                                modifier = Modifier.height(40.dp),
                                                thickness = 1.dp,
                                                color = AppColor.divider1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        Row{
                            BlockButton(
                                state = ButtonState.Stroke,
                                onClick = { navController.popBackStack("home", inclusive = false) },
                                text = "홈으로",
                                modifier = Modifier
                                    .weight(1f)
                                    .height(45.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            BlockButton(
                                state = ButtonState.Default,
                                onClick = { navController.popBackStack() },
                                text = "이어서 학습하기",
                                modifier = Modifier
                                    .weight(1f)
                                    .height(45.dp)
                            )
                        }
                    }
                }
            }
        }
        if (submit is LessonViewModel.SubmitState.Loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        if(submit is LessonViewModel.SubmitState.Failed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Text(
                        text = "오류가 발생했습니다.\n다시 시도해 주세요.",
                        fontFamily = pretendard,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Color(0xFFF2F2F2)
                        ),
                        onClick = { navController.popBackStack() },
                    ) {
                        Text(
                            text = "이전으로",
                            fontFamily = pretendard
                        )
                    }
                }
            }
        }
        popupType?.let { currentType ->
            PopUpUi(
                type = currentType,
                tierName = leagueName,
                level = userLevelResponse?.currentLevel,
                onContinue = {
                    popupType = if (
                        currentType == "level" &&
                        isLeaguePromoted
                    ) {
                        "league"
                    } else {
                        null
                    }
                }
            )
        }
    }
}

@Composable
fun PopUpUi(
    type: String,
    tierName: String? = "브론즈 3",
    level: Int? = 1,
    onContinue: () -> Unit = {}
) {
    val context = LocalContext.current

    val safeTierName = tierName ?: "브론즈 3"
    val safeLevel = level ?: 1

    val imageLoader = remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
    BackHandler(enabled = true) {}
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.lesson_popup_back),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Color.Black.copy(alpha = 0.6f)
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(468.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.popup_card),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.level_confetti)
                    .crossfade(false)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )

            if (type == "league") {
                TierWithGlow(
                    tierId = tierIdFromNameKor(safeTierName),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 36.dp)
                        .fillMaxWidth()
                        .height(204.dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.level_rabbit),
                    contentDescription = "레벨업 캐릭터",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(270.dp)
                        .graphicsLayer {
                            scaleX = 1.25f
                            scaleY = 1.25f
                        },
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (type == "league") {
                        "🎉 축하해요! 🎉"
                    } else {
                        "🎉 레벨업! 🎉" },
                    color = AppColor.bg0,
                    style = AppTypography.Title3
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            AppTypography.Headline2.toSpanStyle()
                        ) {
                            if (type == "league") {
                                append("$safeTierName 티어")
                            } else {
                                append("LV.$safeLevel")
                            }
                        }
                        withStyle(
                            AppTypography.Body2_Nomal.toSpanStyle()
                        ) {
                            if (type == "league") {
                                append(" 로 승급했어요")
                            } else {
                                append(" 으로 올라왔어요")
                            }
                        } },
                    color = AppColor.bg0
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (type == "league") {
                            "다음 티어도 노려봐요. 충분히 가능해요."
                        } else {
                            "한 걸음씩, 확실하게 성장하고 있어요." },
                        color = AppColor.bg0,
                        style = AppTypography.Caption1
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                BlockButton(
                    state = ButtonState.Stroke_Secondary,
                    onClick = onContinue,
                    text = "계속하기",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                )
            }
        }
    }
}
@Composable
fun TierWithGlow(
    tierId: Long?,
    modifier: Modifier = Modifier
) {
    val glowColor = when (tierId) {
        in 1L..3L -> Color(0xFFA25F00)
        in 4L..6L -> Color(0xFFB3B3B3)
        in 7L..9L -> Color(0xFFFFD900)
        in 10L..12L -> Color(0xFF00FFDA)
        in 13L..15L -> Color(0xFF09E5FF)
        else -> Color(0xFFA25F00)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(160.dp)
                .blur(
                    radius = 55.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .background(
                    color = glowColor.copy(alpha = 0.59f),
                    shape = CircleShape
                )
        )

        Image(
            painter = painterFor(tierId),
            contentDescription = "티어",
            modifier = Modifier.size(
                width = 180.dp,
                height = 220.dp
            ),
            contentScale = ContentScale.Fit
        )
    }
}
fun tierIdFromNameKor(name: String?): Long = when (name) {
    "브론즈 3" -> 1L
    "브론즈 2" -> 2L
    "브론즈 1" -> 3L
    "실버 3" -> 4L
    "실버 2" -> 5L
    "실버 1" -> 6L
    "골드 3" -> 7L
    "골드 2" -> 8L
    "골드 1" -> 9L
    "플래티넘 3" -> 10L
    "플래티넘 2" -> 11L
    "플래티넘 1" -> 12L
    "다이아몬드 3" -> 13L
    "다이아몬드 2" -> 14L
    "다이아몬드 1" -> 15L
    else -> -1L
}
val planetName = mapOf(
    1L to "수성",
    2L to "금성",
    3L to "지구",
    4L to "화성",
    5L to "목성",
    6L to "토성",
    7L to "천왕성",
    8L to "해왕성"
)
fun converterInt(id: Long): Int {
    return planetImg[id] ?: R.drawable.algorithm_chapter
}

val planetImg = mapOf(
    1L to R.drawable.data_structure_planet,
    2L to R.drawable.algorithm_planet,
    3L to R.drawable.computer_network_planet,
    4L to R.drawable.database_planet,
    5L to R.drawable.computer_security_planet,
    6L to R.drawable.software_engineering_planet,
    7L to R.drawable.opreating_system_planet,
    8L to R.drawable.programming_language_planet,
)