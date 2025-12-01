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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
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
    chapterName: String,
    accuracy: Float,
    learningTime: Int,
    lessonId: Int
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
                    .height(Responsive.h(80f))
                    .background(Color.White)
            ) {
                Text(
                    text = chapterName,
                    fontSize = Responsive.spH(20f),
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
                        .padding(start = Responsive.w(16f))
                        .size(Responsive.w(24f), Responsive.h(24f))
                        .clickable { navController.navigate("home")},
                    tint = Color(0xFF4D4D4D)
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(Responsive.h(60f))
                .padding(horizontal = Responsive.w(16f)),
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
                    Spacer(Modifier.width(Responsive.w(8f)))
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
                    .padding(horizontal = Responsive.w(16f))
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(Responsive.w(16f))
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFDCDCDC),
                        shape = RoundedCornerShape(Responsive.w(16f))
                    ),
                    contentAlignment = Alignment.Center
                ){
                    Column (verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${unitSummary?.title ?: ""} 학습을 완료했어요!",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = Responsive.spH(20f),
                            color = Color(0xFF030303)

                        )
                        Spacer(Modifier.height(Responsive.h(8f)))
                        Text(
                            text = "다음 레슨을 풀러 가볼까요?",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = Responsive.spH(16f),
                            color = Color(0xFF6D6D6D)
                        )
                        Spacer(Modifier.height(Responsive.h(16f)))
                        Image(
                            painter = painterResource(id = R.drawable.tokki),
                            contentDescription = null,
                            modifier = Modifier.size(Responsive.w(156f), Responsive.h(196f))
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Responsive.w(20f) , vertical = Responsive.h(16f))
                        .weight(1f)
                ) {
                    RoundBox(
                        title = "정답률",
                        value = "${lessonSubmission.accuracy}%",
                        img = R.drawable.books,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(Responsive.w(16f)))

                    RoundBox(
                        title = "풀이시간",
                        value = FormatSeconds(lessonSubmission.learningTime),
                        img = R.drawable.play_button,
                        modifier = Modifier.weight(1f)
                    )
                }

                Box(modifier = Modifier
                    .padding(horizontal = Responsive.w(20f))
                    .weight(1f)
                ) {
                    Button(
                        onClick = {
                            navController.navigate("lessonList/${unitSummary?.unitId}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Responsive.h(60f)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8100B3),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(Responsive.w(10f))
                    ) {
                        Text(
                            "계속하기",
                            fontSize = Responsive.spH(16f),
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(Responsive.h(25f)))
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
                shape = RoundedCornerShape(Responsive.w(16f))
            )
    ){
        Row (
            modifier= Modifier.padding(start = Responsive.w(8f)),
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = img),
                contentDescription = null,
                modifier = Modifier.size(Responsive.w(50f), Responsive.h(50f))
                )
            Spacer(Modifier.width(Responsive.w(8f)))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ){
                Text(
                    text = title,
                    fontWeight = FontWeight.Normal,
                    fontSize = Responsive.spH(14f),
                    fontFamily = pretendard,
                    color = Color.Black
                )
                Text(
                    text = value,
                    fontFamily = pretendard,
                    fontSize = Responsive.spH(20f),
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
    Box(modifier = modifier
        .wrapContentWidth()
        .height(Responsive.h(30f))
        .background(
            color = Color.White,
            shape = RoundedCornerShape(50)
        )
        .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row (
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = img),
                contentDescription = null,
                modifier = Modifier.size(Responsive.w(16f))
            )
            Spacer(Modifier.width(Responsive.w(4f)))
            if(league != "") {
                Text(
                    text = league,
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontSize = Responsive.spH(14f),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8100B3),
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                )
            } else {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(
                            fontWeight = FontWeight.Bold
                        )){
                            append(xp)
                        }
                        withStyle(SpanStyle(
                            fontWeight = FontWeight.Normal
                        )){
                            append("XP")
                        }
                    },
                    style = TextStyle(
                        fontFamily = pretendard,
                        fontSize = Responsive.spH(14f),
                        color = Color(0xFF8100B3),
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                )
            }
        }
    }
}