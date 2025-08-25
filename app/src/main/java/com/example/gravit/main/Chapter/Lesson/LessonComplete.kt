package com.example.gravit.main.Chapter.Lesson

import android.icu.lang.UCharacter.GraphemeClusterBreak.LV
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Home.HomeVMFactory
import com.example.gravit.main.Home.HomeViewModel
import com.example.gravit.ui.theme.pretendard
@Composable
fun LessonComplete(
    navController: NavController,
    chapterId: Int,
    chapterName: String,
    unitId: Int,
    lessonId: Int
){
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
    val league = (ui as? HomeViewModel.UiState.Success)?.data?.league?: "-"
    val lv = (ui as? HomeViewModel.UiState.Success)?.data?.level?: "-"

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF2F2F2))) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .height(80.dp)
                    .background(Color.White)
            ) {
                Text(
                    text = chapterName,
                    fontSize = 20.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .clickable {
                        },
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
                    Box(modifier = Modifier
                        .wrapContentSize()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCDCDC),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.rank_cup),
                                contentDescription = "rank mark",
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = league,
                                fontSize = 14.sp,
                                fontFamily = pretendard,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8100B3)
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier
                        .wrapContentHeight()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCDCDC),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .weight(1f)
                        .padding(6.dp),
                    ){
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(
                                    fontWeight = FontWeight.Normal
                                )){
                                    append("LV")
                                }
                                withStyle(SpanStyle(
                                    fontWeight = FontWeight.Bold
                                )){
                                    append("${lv}")
                                }
                            },
                            fontFamily = pretendard,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.padding(start = 14.dp)
                        )
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(5f)){
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    )
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
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${planetName[chapterId].toString()}에 한발 더 가까워졌어요!",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color(0xFF030303)

                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "${chapterName}의 ${unitId}번째 단계를 학습을 완료했어요",
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color(0xFF6D6D6D)
                        )
                        Spacer(Modifier.height(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.tokki),
                            contentDescription = null,
                            modifier = Modifier.size(156.dp, 196.dp)
                        )
                    }
                }
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(2f)){
                Row(modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCDCDC),
                            shape = RoundedCornerShape(16.dp)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDCDCDC),
                            shape = RoundedCornerShape(16.dp)
                        )
                    )
                }
            }

            Button(
                onClick = {
                    navController.navigate("home")
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8100B3),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("계속하기",
                    fontSize = 16.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Bold)
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

@Preview(showBackground = true)
@Composable
fun StudyCompletePreview() {
    val navController = rememberNavController()
    LessonComplete(navController, chapterId = 1, chapterName = "자료구조", unitId = 1, lessonId = 1)
}