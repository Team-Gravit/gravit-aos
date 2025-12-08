package com.example.gravit.main.Study.Lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.gravit.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.api.LessonSubmissionSaveRequest
import com.example.gravit.api.ProblemSubmissionRequests
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.Responsive
import com.example.gravit.main.Study.Problem.FormatSeconds
import com.example.gravit.main.Home.LevelGauge
import com.example.gravit.main.Study.Problem.LessonVMFactory
import com.example.gravit.main.Study.Problem.LessonViewModel
import com.example.gravit.ui.theme.pretendard
@Composable
fun LessonComplete(
    navController: NavController,
    unitTitle: String,
    accuracy: Float,
    learningTime: Int,
    lessonId: Int,
){
    val entry = navController.previousBackStackEntry
    val problemList = entry?.savedStateHandle?.get<List<ProblemSubmissionRequests>>("problemList")
    val lessonSubmission = LessonSubmissionSaveRequest(lessonId, learningTime, accuracy)
    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

    LaunchedEffect(Unit) {
        vm.submitResults(lessonSubmission, problemList)
    }

    val submit by vm.submit.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(submit) {
        when (submit) {
            LessonViewModel.SubmitState.SessionExpired ->  {
                navigated = true
                navController.navigate("error/401") {
                    launchSingleTop = true; restoreState = false
                }
            }
            LessonViewModel.SubmitState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    launchSingleTop = true; restoreState = false
                }
            }
            else -> Unit
        }
    }
    val s = (submit as? LessonViewModel.SubmitState.Success)?.data
    val userLevelResponse = s?.userLevelResponse
    val unitSummary = s?.unitSummary

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(WindowInsets.statusBars.asPaddingValues())
        .background(Color(0xFFF2F2F2))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color.White)
            ) {
                Text(
                    text = unitTitle,
                    fontSize = 20.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF030303)
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(24.dp)
                        .clickable { navController.navigate("home")},
                    tint = Color(0xFF4D4D4D)
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ){
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PillShape(
                        img = R.drawable.rank_cup,
                        league = s?.leagueName ?: "Bronze1"
                    )
                    Spacer(Modifier.width(8.dp))
                    LevelGauge(
                        lv = userLevelResponse?.currentLevel ?: 1,
                        xp = userLevelResponse?.xp ?: 0,
                    )
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)){
                Box(modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFDCDCDC),
                        shape = RoundedCornerShape(16.dp)
                    ),
                    contentAlignment = Alignment.Center
                ){
                    Column (verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${unitSummary?.title} 학습을 완료했어요!",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color(0xFF030303)

                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "다음 레슨을 풀러 가볼까요?",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color(0xFF6D6D6D)
                        )
                        Spacer(Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.tokki),
                            contentDescription = null,
                            modifier = Modifier
                                .width(156.dp)
                                .height(196.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(0.7f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp , vertical = 16.dp)
                        .weight(1f)
                ) {
                    RoundBox(
                        title = "정답률",
                        value = "${lessonSubmission.accuracy}%",
                        img = R.drawable.books,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    RoundBox(
                        title = "풀이시간",
                        value = FormatSeconds(lessonSubmission.learningTime),
                        img = R.drawable.play_button,
                        modifier = Modifier.weight(1f)
                    )
                }

                Box(modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .weight(1f)
                ) {
                    Button(
                        onClick = {
                            navController.navigate("lessonList/${unitSummary?.unitId}/${unitSummary?.title}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(63.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8100B3),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "계속하기",
                            fontSize = 16.sp,
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(25.dp))
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
    }
}

@Composable
fun RoundBox(
    title: String,
    value: String,
    img: Int,
    modifier: Modifier
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )

    ){
        Row (
            modifier= Modifier.padding(start = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = img),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
            Spacer(Modifier.width(1.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ){
                Text(
                    text = title,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    fontFamily = pretendard,
                    color = Color.Black
                )
                Text(
                    text = value,
                    fontFamily = pretendard,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun PillShape(
    modifier: Modifier = Modifier,
    img: Int,
    league: String = "",
    xp: String = ""
){
    val config = LocalConfiguration.current
    // Figma / 디자인 기준 해상도
    val designWidth = 360f
    val designHeight = 740f

    val scaleW = config.screenWidthDp.toFloat() / designWidth
    val scaleH = config.screenHeightDp.toFloat() / designHeight

    fun dw(v: Float) = (v * scaleW).dp
    fun dh(v: Float) = (v * scaleH).dp

    Box(
        modifier = modifier
            .wrapContentWidth()
            .height(dh(25f))
            .background(
                color = Color.White,
                shape = RoundedCornerShape(50)
            )
            .padding(
                horizontal = dw(6f),
                vertical = dh(4f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = img),
                contentDescription = null,
                modifier = Modifier.size(dh(16f))
            )
            Spacer(Modifier.width(dw(4f)))
            if (league.isNotEmpty()) {
                Text(
                    text = league,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8100B3),
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier.padding(end = dw(2f))
                )
            } else {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(xp)
                        }
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("XP")
                        }
                    },
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontSize = 14.sp,
                        color = Color(0xFF8100B3),
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    )
                )
            }
        }
    }
}