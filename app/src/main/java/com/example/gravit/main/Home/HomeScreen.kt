package com.example.gravit.main.Home

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.Responsive
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Chapter.Lesson.PillShape
import com.example.gravit.main.Chapter.Lesson.RoundBox
import com.example.gravit.ui.theme.pretendard
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(factory = HomeVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }
    when (ui) {
        HomeViewModel.UiState.SessionExpired -> {
            navController.navigate("login choice") {
                popUpTo(0); launchSingleTop = true; restoreState = false
            }
        }
        HomeViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> Unit
    }
    val home = (ui as? HomeViewModel.UiState.Success)?.data

    val dateFormat = remember { SimpleDateFormat("yyyy. MM. dd (E)", Locale.KOREAN) }
    val today = remember { dateFormat.format(Date()) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF2F2F2))
            )
            Image(
                painter = painterResource(id = R.drawable.main_back),
                contentDescription = "main back",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Responsive.h(266f)),
                contentScale = ContentScale.FillWidth

            )
        }
        Column {
            Spacer(Modifier.height(Responsive.h(11f)))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Responsive.h(70f))

            ) {
                Image(
                    painter = painterResource(id = R.drawable.gravit_main_logo),
                    contentDescription = "gravit typo",
                    modifier = Modifier
                        .size(Responsive.w(133f), Responsive.w(32f))
                        .align(Alignment.CenterStart)
                        .padding(start = Responsive.w(19f))
                )
            }
            Spacer(Modifier.height(Responsive.h(60f)))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Responsive.w(16f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Responsive.h(84f))
                ) {
                    val nickname = home?.nickname
                    Column {
                        CustomText(
                            text = "어서오세요,",
                            fontWeight = FontWeight.Bold,
                            fontSize = Responsive.spH(28f),
                            color = Color.White,
                            shadow = Shadow(
                                color = Color(0xFF000000),
                                offset = Offset(0f, 2f),
                                blurRadius = 4f
                            )
                        )
                        Spacer(modifier = Modifier.height(Responsive.h(12f)))
                        CustomText(
                            text = if (nickname.isNullOrBlank()) "" else "$nickname!",
                            fontWeight = FontWeight.Bold,
                            fontSize = Responsive.spH(28f),
                            color = Color.White,
                            shadow = Shadow(
                                color = Color(0xFF000000),
                                offset = Offset(0f, 2f),
                                blurRadius = 4f
                            )
                        )
                    }
                    CustomText(
                        text = today,
                        fontSize = Responsive.spH(12f),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.align(Alignment.BottomEnd),
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(Responsive.h(8f)))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Row {
                            val level = home?.level ?: 1
                            val xp = home?.xp ?: 0
                            val league = home?.leagueName ?: "Bronze 1"
                            PillShape(img = R.drawable.rank_cup, league = league)

                            Spacer(modifier = Modifier.width(Responsive.w(8f)))
                            PillShape(img = R.drawable.xp_mark, xp = xp.toString())

                            Spacer(modifier = Modifier.width(Responsive.w(8f)))
                            LevelGauge(lv = level, xp = xp)
                        }

                        Spacer(modifier = Modifier.height(Responsive.h(16f)))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Responsive.h(186f))
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .size(Responsive.w(160f), Responsive.h(186f))
                                        .clip(RoundedCornerShape(Responsive.h(16f)))
                                        .background(Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(
                                                horizontal = Responsive.w(16f),
                                                vertical = Responsive.h(16f)
                                            )
                                            .fillMaxSize()
                                    ) {
                                        CustomText(
                                            text = "오늘의 미션🔥",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = Responsive.spH(20f),
                                            modifier = Modifier.size(
                                                Responsive.w(128f),
                                                Responsive.h(24f)
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(Responsive.h(12f)))

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
                                        Spacer(modifier = Modifier.height(Responsive.h(12f)))

                                        val mission = home?.missionName
                                        val missionXp = home?.awardXp
                                        val isCompleted = home?.isCompleted
                                        isCompleted?.let {
                                            if(!it){
                                                Row{
                                                    CustomText(
                                                        text = "•",
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = Responsive.spH(16f),
                                                        color = Color(0xFF222124)
                                                    )
                                                    CustomText(
                                                        text = mission,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = Responsive.spH(16f),
                                                        color = Color(0xFF222124)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(Responsive.h(3f)))

                                                CustomText(
                                                    text = "완료시 ${missionXp}XP",
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = Responsive.spH(12f),
                                                    color = Color(0xFF494949),
                                                    modifier = Modifier.padding(start = Responsive.w(8f))
                                                )

                                                Spacer(modifier = Modifier.height(Responsive.h(12f)))

                                                Button(
                                                    modifier = Modifier.size(
                                                        Responsive.w(128f),
                                                        Responsive.h(39f)
                                                    ),
                                                    onClick = {navController.navigate("chapter") {
                                                        launchSingleTop = true
                                                    }},
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
                                                        fontSize = Responsive.spH(16f),
                                                        color = Color.Black,
                                                    )
                                                }
                                            } else {
                                                Image(
                                                    painter = painterResource(id = R.drawable.mission_complete),
                                                    contentDescription = "mission completed"
                                                )
                                            }
                                        }

                                    }
                                }

                                Spacer(modifier = Modifier.width(Responsive.w(8f)))

                                Column {
                                    RoundBox(
                                        title = "행성 정복률",
                                        value = "${home?.planetConquestRate}%",
                                        img = R.drawable.rocket_main,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    RoundBox(
                                        title = "연속 학습일",
                                        value = "${home?.consecutiveDays}일",
                                        img = R.drawable.fire,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(Responsive.h(16f)))

                        val chapterId = home?.chapterId ?: 0
                        val chapterName = home?.chapterName
                        val chapterDescription = home?.chapterDescription
                        val totalUnits = home?.totalUnits
                        val completedUnits = home?.completedUnits
                        val bgResId = previousImg[chapterId] ?: 0

                        PreviousButton(
                            chapterId = chapterId,
                            chapterName = chapterName,
                            completedUnits = completedUnits,
                            backgroundImg = bgResId,
                            totalUnits = totalUnits,
                            onClick = {
                                navController.navigate("chapter") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun View() {
    val navController = rememberNavController()
   HomeScreen(navController)
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

