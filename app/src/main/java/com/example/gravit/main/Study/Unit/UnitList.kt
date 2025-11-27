package com.example.gravit.main.Study.Unit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.ui.theme.pretendard


@Composable
fun UnitList(
    chapterId: Int, //api 연동할 때 쓰셈
    navController: NavController,
    onSessionExpired: () -> Unit //세션 만료 함수
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.unitlist_back),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .padding(start = 18.dp)
                            .size(20.dp),
                        //.clickable { navController.popBackStack() },
                        tint = Color.White
                    )

                    Spacer(Modifier.width(18.dp))

                    Text(
                        text = "챕터이름",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = pretendard,
                        color = Color.White
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "유닛 리스트",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        fontFamily = pretendard,
                        textAlign = TextAlign.Start,
                        color = Color.White,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(width = 1.dp, color = Color(0xFF8B69FF))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.4f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .padding(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row {
                                Text(
                                    text = "UNIT 순서",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFamily = pretendard,
                                    textAlign = TextAlign.Start,
                                    color = Color(0xFFFFFFCC),

                                    )

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = "유닛 이름",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    fontFamily = pretendard,
                                    textAlign = TextAlign.Start,
                                    color = Color(0xFFFFFFCC),

                                    )
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "100%",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    fontFamily = pretendard,
                                    color = Color(0xFFFFFFCC),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(40.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(15.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                ) {
                                    //프로그래스 바 넣어야징
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnitListPreview() {
    val navController = rememberNavController()
    UnitList(1, navController, {})
}