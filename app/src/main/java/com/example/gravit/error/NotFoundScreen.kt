package com.inuappcenter.gravit.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.main.Study.Problem.ReportButton
import com.inuappcenter.gravit.R

@Composable
fun NotFoundScreen(
    navController: NavController,
){
    ErrorScreen(
        img = R.drawable.img404,
        title = "페이지를 찾을 수 없어요.",
        content = "잘못된 주소이거나 삭제된 페이지입니다.\n주소를 확인하시거나 홈으로 이동해 주세요.",
        onClick1 = { navController.popBackStack("home", inclusive = false) },
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = img),
                contentDescription = "404 img"
            )

            Spacer(Modifier.height(30.dp))

            Text(
                text = title,
                style = AppTypography.Heading1,
                color = AppColor.text1
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = content,
                style = AppTypography.Body2_Normal,
                color = AppColor.text4,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(30.dp))
        }

        ReportButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 20.dp
                ),
            onClick1 = onClick2,
            text1 = "돌아가기",
            modifier1 = Modifier
                .height(50.dp)
                .weight(1f),
            onClick2 = onClick1,
            text2 = "메인으로",
            modifier2 = Modifier
                .height(50.dp)
                .weight(1f),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotFoundScreenPreview() {
    val navController = rememberNavController()
    NotFoundScreen(navController)
}