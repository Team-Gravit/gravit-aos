package com.inuappcenter.gravit.main.Study.Lesson

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.gravit.share.LoadFailedDialog
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
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

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun LessonComplete(
    navController: NavController,
    accuracy: Int,
    learningTime: Int,
    lessonId: Long,
    chapterId: Long
) {
    val homeEntry = navController.getBackStackEntry("home")
    val problemList = homeEntry.savedStateHandle.get<ArrayList<ProblemSubmissionSaveRequests>>("problemList")
    val lessonSubmission = LessonSubmissionSaveRequest(lessonId, learningTime, accuracy)

    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(factory = LessonVMFactory(RetrofitInstance.api, context))
    val submit by vm.submit.collectAsState()

    fun submitLessonResult() {
        val list = problemList ?: return

        vm.submitResults(
            lessonSubmissionSaveRequest = lessonSubmission,
            problemSubmissionRequests = list
        ) { success ->
            if (success) {
                homeEntry.savedStateHandle
                    .remove<ArrayList<ProblemSubmissionSaveRequests>>(
                        "problemList"
                    )
            }
        }
    }

    LaunchedEffect(Unit) { submitLessonResult() }

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(submit) {
        when (submit) {
            LessonViewModel.SubmitState.SessionExpired -> {
                if (!navigated) {
                    navigated = true

                    navController.navigate("error/401") {
                        launchSingleTop = true
                    }
                }
            }

            LessonViewModel.SubmitState.NotFound -> {
                if (!navigated) {
                    navigated = true

                    navController.navigate("error/404") {
                        launchSingleTop = true
                    }
                }
            }

            else -> {
                navigated = false
            }
        }
    }

    val successState = submit as? LessonViewModel.SubmitState.Success
    val leagueName = successState?.data?.leagueName
    val userLevelResponse = successState?.data?.userLevelResponse
    val unitSummary = successState?.data?.unitSummaryResponse
    val isLeaguePromoted = successState?.isLeaguePromoted ?: false

    var popupType by rememberSaveable { mutableStateOf<String?>("level") }

    LaunchedEffect(submit) {
        val success =
            submit as? LessonViewModel.SubmitState.Success
                ?: return@LaunchedEffect

        popupType = when {
            success.isLevelUp -> "level"
            success.isLeaguePromoted -> "league"
            else -> null
        }
    }

    StatusBarStyle(darkIcons = false)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (submit) {
            LessonViewModel.SubmitState.Idle,
            LessonViewModel.SubmitState.Loading -> {
                LessonCompleteSkeleton()
            }

            is LessonViewModel.SubmitState.Success -> {
                LessonCompleteSuccessUI(
                    navController = navController,
                    accuracy = accuracy,
                    learningTime = learningTime,
                    chapterId = chapterId,
                    unitTitle = unitSummary?.title.orEmpty(),
                    xp = userLevelResponse?.xp ?: 0,
                    nextLevel = userLevelResponse?.nextLevel ?: 2
                )
            }

            LessonViewModel.SubmitState.Failed -> {
                LessonCompleteSkeleton()

                LoadFailedDialog(
                    onRetryClick = {
                        submitLessonResult()
                    }
                )
            }

            LessonViewModel.SubmitState.SessionExpired,
            LessonViewModel.SubmitState.NotFound -> Unit
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
private fun LessonCompleteSuccessUI(
    navController: NavController,
    accuracy: Int,
    learningTime: Int,
    chapterId: Long,
    unitTitle: String,
    xp: Int,
    nextLevel: Int
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg1)
            .navigationBarsPadding()
    ) {
        val designWidth = 420.dp
        val designHeight = 740.dp

        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val widthScale = screenWidth / designWidth
        val heightScale = screenHeight / designHeight

        val contentScale = minOf(
            widthScale,
            heightScale
        ).coerceIn(1f, 1.5f)

        val scaledWidth = designWidth * contentScale
        val scaledHeight = designHeight * contentScale

        Image(
            painter = painterResource(R.drawable.lesson_result_bg_mobile),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier.height(
                    (screenHeight - scaledHeight).coerceAtLeast(0.dp)
                )
            )

            Box(
                modifier = Modifier
                    .width(scaledWidth)
                    .height(scaledHeight),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .width(designWidth)
                        .height(designHeight)
                        .graphicsLayer {
                            scaleX = contentScale
                            scaleY = contentScale
                            transformOrigin = TransformOrigin(
                                pivotFractionX = 0.5f,
                                pivotFractionY = 0f
                            )
                        }
                ) {
                    Image(
                        painter = painterResource(
                            R.drawable.lesson_result_rabbit
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = 1.5f
                                scaleY = 1.5f
                                translationY = 24.dp.toPx()
                            },
                        contentScale = ContentScale.FillWidth
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(
                                top = WindowInsets.statusBars
                                    .asPaddingValues()
                                    .calculateTopPadding() + 75.dp
                            )
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${planetName[chapterId].orEmpty()} 정복에 더 가까워졌어요!",
                            style = AppTypography.Heading1,
                            color = AppColor.text1w,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "$unitTitle 학습 완료",
                            style = AppTypography.Label2,
                            color = AppColor.text2w
                        )

                        Spacer(
                            modifier = Modifier.height(40.dp)
                        )

                        val planetPainter = painterResource(
                            converterInt(chapterId)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(end = 22.dp)
                                .size(113.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = planetPainter,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                colorFilter = ColorFilter.tint(
                                    color = AppColor.bg0.copy(alpha = 0.2f),
                                    blendMode = BlendMode.SrcIn
                                )
                            )

                            Image(
                                painter = planetPainter,
                                contentDescription = null,
                                modifier = Modifier.size(107.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    LessonCompleteBottomContent(
                        navController = navController,
                        accuracy = accuracy,
                        learningTime = learningTime,
                        xp = xp,
                        nextLevel = nextLevel,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 24.dp
                            )
                    )
                }
            }
        }
    }
}
@Composable
private fun LessonCompleteBottomContent(
    navController: NavController,
    accuracy: Int,
    learningTime: Int,
    xp: Int,
    nextLevel: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${xp}XP",
                style = AppTypography.Label1,
                color = AppColor.text3
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "LV${nextLevel}까지",
                style = AppTypography.Label1,
                color = AppColor.Main1
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        val progress = calculateXpProgress(xp)

        RoundedGauge(
            rate = progress,
            height = 8.dp,
            width = 0.dp,
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFFBF1FF)
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0),
            contentAlignment = Alignment.Center
        ) {
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
                        color = if (
                            index == 0 && isLeague
                        ) {
                            AppColor.Main1
                        } else {
                            AppColor.text1
                        }
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

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            BlockButton(
                state = ButtonState.Stroke,
                onClick = {
                    navController.popBackStack(
                        "home",
                        inclusive = false
                    )
                },
                text = "홈으로",
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            BlockButton(
                state = ButtonState.Default,
                onClick = {
                    navController.popBackStack()
                },
                text = "이어서 학습하기",
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
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
                .background(Color.Black.copy(alpha = 0.6f))
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(328.dp)
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
                                "🎉 레벨업! 🎉"
                            },
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
                                    AppTypography.Body2_Normal.toSpanStyle()
                                ) {
                                    if (type == "league") {
                                        append(" 로 승급했어요")
                                    } else {
                                        append(" 으로 올라왔어요")
                                    }
                                }
                            },
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
                                    "한 걸음씩, 확실하게 성장하고 있어요."
                                },
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