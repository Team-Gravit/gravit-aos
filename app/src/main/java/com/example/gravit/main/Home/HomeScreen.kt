package com.example.gravit.main.Home

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Chapter.RoundedGauge
import com.example.gravit.ui.theme.LocalScreenHeight
import com.example.gravit.ui.theme.LocalScreenWidth
import com.example.gravit.ui.theme.mbc1961
import com.example.gravit.ui.theme.pretendard
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(
        factory = HomeVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }
    when (ui) {
        HomeViewModel.UiState.SessionExpired -> {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        }

        else -> Unit
    }


    val dateFormat = remember { SimpleDateFormat("yyyy. MM. dd (E)", Locale.KOREAN) }
    val today = remember { dateFormat.format(Date()) }


    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val previousImg: Map<Int, Int> = mapOf(
        0 to R.drawable.previous_null,
        1 to R.drawable.data_structure,
        2 to R.drawable.algorithm,
        3 to R.drawable.computer_network,
        4 to R.drawable.operating_system,
        5 to R.drawable.database,
        6 to R.drawable.computer_security,
        7 to R.drawable.programming_language,
        8 to R.drawable.programming_language
    )

    CompositionLocalProvider(
        LocalScreenWidth provides screenWidth,
        LocalScreenHeight provides screenHeight
    ) {

        Box(
            modifier = Modifier.fillMaxSize()

        ) {
            Box (
                modifier = Modifier.fillMaxSize()
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF2F2F2))
                )

                Image( //배경 이미지
                    painter = painterResource(id = R.drawable.main_back),
                    contentDescription = "main back",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()

                )

            }


            Column {
                Box(
                    modifier = Modifier
                        .padding(top = screenHeight * (24f / 740f))
                        .fillMaxWidth()
                        .height(screenHeight * (70f / 740f))
                ) {
                    Image(
                        //로고 이미지
                        painter = painterResource(id = R.drawable.gravit_main_logo),
                        contentDescription = "gravit typo",
                        modifier = Modifier
                            .padding(start = screenWidth * (19f / 360f))
                            .align(Alignment.CenterStart)
                    )

                }

                Box(
                    modifier = Modifier
                        .padding(top = screenHeight * (60f / 740f))
                        .padding(horizontal = screenWidth * (16f / 360f))
                        .fillMaxWidth()
                        .height(screenHeight * (84f / 740f))

                ) {
                    val nickname = (ui as? HomeViewModel.UiState.Success)?.data?.nickname
                    Column {
                        CustomText(
                            text = "어서오세요,",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color(0xFF000000),
                                offset = Offset(0f, 2f),
                                blurRadius = 4f
                            )
                        )

                        Spacer(modifier = Modifier.height(screenHeight * (12f / 740f)))

                        CustomText(
                            text = if (nickname.isNullOrBlank()) "" else "$nickname!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.align(Alignment.BottomEnd),
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = screenHeight * (8f / 740f))
                        .padding(horizontal = screenWidth * (16f / 360f))
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center

                ) {
                    Column {
                        Row {
                            PillShape { //리그
                                Image(
                                    painter = painterResource(id = R.drawable.rank_cup),
                                    contentDescription = "rank mark",
                                    modifier = Modifier
                                        .size(screenWidth * (16f / 360f))

                                )

                                Spacer(modifier = Modifier.width(screenWidth * (4f / 360f)))

                                val league = (ui as? HomeViewModel.UiState.Success)?.data?.league ?: "—"
                                CustomText(
                                    text = league,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF8100B3)
                                )
                            }

                            Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))

                            PillShape(width = 76f) { //XP
                                Image(
                                    painter = painterResource(id = R.drawable.xp_mark),
                                    contentDescription = "rank mark",
                                    modifier = Modifier
                                        .size(screenWidth * (16f / 360f))
                                )

                                Spacer(modifier = Modifier.width(screenWidth * (4f / 360f)))

                                val xp = (ui as? HomeViewModel.UiState.Success)?.data?.xp ?: 0
                                CustomText(
                                    text = xp.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF8100B3)
                                )

                                Spacer(modifier = Modifier.width(screenWidth * (2f / 360f)))

                                CustomText(
                                    text = "XP",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Color(0xFF8100B3)
                                )
                            }

                            Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))
                            //LV
                            val level = (ui as? HomeViewModel.UiState.Success)?.data?.level ?: 0
                            PillShape(width = 169f) {
                                CustomText(
                                    text = "LV",
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                CustomText(
                                    text = level.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(screenHeight * (16f / 740f)))

                        Box(
                            modifier = Modifier.size(screenWidth * (328f /360f), screenHeight * (186f / 740f))

                        ) {
                            Row {
                                Box( //오늘의 미션
                                    modifier = Modifier
                                        .size(
                                            screenWidth * (160f / 360f),
                                            screenHeight * (186f / 740f)
                                        )
                                        .clip(shape = RoundedCornerShape(16.dp))
                                        .background(Color.White)

                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(
                                                horizontal = screenWidth * (16f / 360f),
                                                vertical = screenHeight * (16f / 740f)
                                            )
                                            .fillMaxSize(),
                                    ) {

                                        CustomText(
                                            text = "오늘의 미션🔥",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp,
                                            modifier = Modifier.size(screenWidth * (128f / 360f) , screenHeight * (24f / 740f))
                                        )

                                        Spacer(modifier = Modifier.height(screenHeight * (12f / 740f)))

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
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)),
                                                cap = StrokeCap.Butt
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(screenHeight * (12f / 740f)))

                                        CustomText(
                                            text = "•자료구조 챕터3",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = Color(0xFF222124)
                                        )

                                        CustomText(
                                            text = "완료",
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = Color(0xFF222124),
                                            modifier = Modifier.padding(start = screenWidth * (8f / 360f))
                                        )

                                        Spacer(modifier = Modifier.height(screenHeight * (3f / 740f)))

                                        CustomText(
                                            text = "완료시 150XP",
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Color(0xFF494949),
                                            modifier = Modifier.padding(start = screenWidth * (8f / 360f))
                                        )

                                        Spacer(modifier = Modifier.height(screenHeight * (12f / 740f)))

                                        Button(
                                            modifier = Modifier.size(screenWidth * (128f / 360f) , screenHeight * (39f / 740f)),
                                            onClick = {},
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFF2F2F2),
                                                contentColor = Color.Black
                                            ),
                                        ) {
                                            CustomText(
                                                text = "학습하러 가기",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = Color.Black
                                            )
                                        }
                                    }

                                }

                                Spacer(modifier = Modifier.width(screenWidth * (8f/ 360f)))

                                Column {
                                    RoundRect(
                                        imageRes = R.drawable.rocket_main,
                                        title = "행성 정복률",
                                        value = "임시%"
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    RoundRect(
                                        imageRes = R.drawable.fire,
                                        title = "연속 학습일",
                                        value = "임시일"
                                    )
                                }
                            }

                        }

                        Spacer(modifier = Modifier.height(screenHeight * (16f / 740f)))

                        val success = ui as? HomeViewModel.UiState.Success
                        val recent  = success?.data?.recentLearningSummaryResponse

                        val chapterId = (recent?.chapterId ?: 0).toInt()

                        val (chapterName, totalUnits, completedUnits) =
                            if (chapterId == 0) {
                                Triple("최근에 진행한 학습 정보가 없습니다", 0, 0)
                            } else {
                                Triple(
                                    recent?.chapterName ?: "",
                                    recent?.totalUnits ?: 0,
                                    recent?.completedUnits ?: 0
                                )
                            }

                        val bgResId = previousImg[chapterId] ?: 0
                        PreviousButton(
                            chapterId = chapterId,
                            chapter = chapterName,
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
private fun CustomText (
    modifier: Modifier = Modifier,
    text: String,
    fontFamily: FontFamily = pretendard,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    color: Color = Color.Black,
    shadow: Shadow? = null,
) {
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

@Composable
fun PillShape(
    modifier: Modifier = Modifier,
    width: Float = 67f,
    height: Float = 25f,
    backgroundColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(50),
    content: @Composable RowScope.() -> Unit
) {
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    Box(
        modifier = modifier
            .size(width = screenWidth * (width / 360f), height = screenHeight * (height / 740f))
            .background(backgroundColor, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun RoundRect(
    imageRes: Int,
    title: String,
    value: String
) {
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    Box(
        modifier = Modifier
            .size(screenWidth * (160f / 360f), screenHeight * (89f / 740f))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {
        Row (
            modifier = Modifier.padding(vertical = screenHeight * (20f / 740f))
        ){
            Image(
                modifier = Modifier
                    .padding(start = screenWidth * (8f / 360f))
                    .size(screenWidth * (50f / 360f)),
                contentDescription = null,
                painter = painterResource(id = imageRes)
            )
            Spacer(modifier = Modifier.width(screenWidth * (8f / 360f)))

            Column {
                CustomText(
                    text = title,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(screenWidth * (8f / 360f)))
                CustomText(
                    text = value,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
fun PreviousButton(
    chapterId: Int,
    backgroundImg: Int,
    chapter: String,
    completedUnits: Int,
    totalUnits: Int,
    onClick: () -> Unit,
) {
    val screenWidth = LocalScreenWidth.current
    val screenHeight = LocalScreenHeight.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = backgroundImg),
            contentDescription = null,
        )
        Column (
            modifier = Modifier.padding(
                horizontal = screenWidth * (16f / 360f),
                vertical = screenHeight * (16f / 740f)
            )
        ) {

            if (chapterId == 0) {
                Row {
                    CustomText(
                        text = "새로운 학습을 시작하기",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = mbc1961,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_forward_ios_24),
                        contentDescription = null,
                        modifier = Modifier,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(screenHeight * (8f/ 740f)))

                CustomText(
                    text = chapter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    color = Color.White,
                )
            } else {
                CustomText(
                    text = "직전학습 이어서 하기",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(screenHeight * (8f/ 740f)))

                CustomText(
                    text = chapter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    fontFamily = mbc1961,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(screenHeight * (23f/ 740f)))

                RoundedGauge(
                    height = screenHeight * (10f / 740f),
                    width = screenWidth * (271f / 360f),
                    completedUnits = completedUnits,
                    totalUnits = totalUnits
                )
            }

        }

    }
}


