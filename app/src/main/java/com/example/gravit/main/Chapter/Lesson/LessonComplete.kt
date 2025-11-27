package com.example.gravit.main.Chapter.Lesson

import android.util.Log
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
import com.example.gravit.Responsive
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Home.HomeVMFactory
import com.example.gravit.main.Home.HomeViewModel
import com.example.gravit.main.Home.LevelGauge
import com.example.gravit.ui.theme.pretendard
@Composable
fun LessonComplete(
    navController: NavController,
    chapterId: Int,
    chapterName: String,
    unitId: Int,
    lessonId: Int,
    accuracy: Int,
    learningTime: Int,
    onSessionExpired: () -> Unit
){
    Log.d("LessonComplete", "화면 진입")
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(
        factory = HomeVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }

    var navigated by remember { mutableStateOf(false) }
    LaunchedEffect(ui) {
        when (ui) {
            HomeViewModel.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            HomeViewModel.UiState.NotFound ->  {
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            HomeViewModel.UiState.Failed ->  {
                navigated = true
                onSessionExpired()
            }

            else -> Unit
        }
    }
    val league = (ui as? HomeViewModel.UiState.Success)?.data?.leagueName?: "브론즈 1"
    val lv = (ui as? HomeViewModel.UiState.Success)?.data?.userLevelDetail?.level ?: 1
    val xp = (ui as? HomeViewModel.UiState.Success)?.data?.userLevelDetail?.xp ?: 0

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
                        .clickable { navController.navigate("units/${chapterId}")},
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
                        league = league
                    )
                    Spacer(Modifier.width(Responsive.w(8f)))
                    LevelGauge(
                        lv = lv,
                        xp = xp,
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
                            text = "${planetName[chapterId].toString()}에 한발 더 가까워졌어요!",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = Responsive.spH(20f),
                            color = Color(0xFF030303)

                        )
                        Spacer(Modifier.height(Responsive.h(8f)))
                        Text(
                            text = "${chapterName}의 ${unitId}번째 단계를 학습을 완료했어요",
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
                        value = "${accuracy}%",
                        img = R.drawable.books,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(Responsive.w(16f)))

                    RoundBox(
                        title = "풀이시간",
                        value = FormatSeconds(learningTime),
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
                            navController.navigate("units/${chapterId}")
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
        if (ui is HomeViewModel.UiState.Loading) {
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


private val planetName = mapOf(
    1 to "지구",
    2 to "수성",
    3 to "금성",
    4 to "화성",
    5 to "목성",
    6 to "토성",
    7 to "천왕성",
    8 to "해왕성",
)

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