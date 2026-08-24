package com.inuappcenter.gravit.main.Study.Lesson

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.inuappcenter.gravit.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.api.LessonSubmissionSaveRequest
import com.inuappcenter.gravit.api.ProblemSubmissionSaveRequests
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Home.RoundedGauge
import com.inuappcenter.gravit.main.Study.Problem.FormatSeconds
import com.inuappcenter.gravit.main.Study.Problem.LessonVMFactory
import com.inuappcenter.gravit.main.Study.Problem.LessonViewModel
import com.inuappcenter.gravit.main.User.RankInfo
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
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

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
    val s = (submit as? LessonViewModel.SubmitState.Success)?.data
    val userLevelResponse = s?.userLevelResponse
    val unitSummary = s?.unitSummaryResponse

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
                        RoundedGauge(
                            height = 8.dp,
                            width = 0.dp,
                            rate = userLevelResponse?.xp?.toDouble()?: 0.0,
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
    }
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