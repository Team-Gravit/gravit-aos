package com.example.gravit.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.main.Study.Problem.ReportButton
import com.example.gravit.ui.theme.pretendard

@Composable
fun NotFoundScreen(
    navController: NavController,
){
    ErrorScreen(
        img = R.drawable.img404,
        title = "페이지를 찾을 수 없어요.",
        content = "입력한 주소가 잘못되었거나,\n페이지가 이동되었거나 삭제되었을 수 있어요.\n주소를 다시 확인하시거나,\n홈으로 돌아가 서비스를 다시 이용해 주세요.",
        onClick1 = { navController.navigate("home") },
        onClick2 = {navController.popBackStack()}
        )
}


@Composable
fun ErrorScreen(
    img: Int,
    title: String,
    content: String,
    onClick1: () -> Unit,
    onClick2: () -> Unit,
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = img),
                contentDescription = "404 img",
                modifier = Modifier.size(276.dp, 281.dp)
            )
            Spacer(Modifier.height(30.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = pretendard,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF383838),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = content,
                style = TextStyle(
                    fontFamily = pretendard,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6D6D6D),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            )
        }
        Row (
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
        ){
            ReportButton(
                text = "이전으로",
                bgC = Color(0xFFA8A8A8),
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f),
                onClick = onClick2,
            )
            Spacer(Modifier.width(8.dp))
            ReportButton(
                text = "메인으로",
                bgC = Color(0xFF8100B3),
                modifier = Modifier
                    .height(50.dp)
                    .weight(1f),
                onClick = onClick1,
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val navController = rememberNavController()
    NotFoundScreen(navController)
}