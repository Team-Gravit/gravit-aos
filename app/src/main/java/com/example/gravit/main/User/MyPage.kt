package com.inuappcenter.gravit.main.User

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.InlineButton
import com.example.gravit.ui.theme.InlineButtonSize
import com.example.gravit.ui.theme.InlineButtonState
import com.example.gravit.ui.theme.PrimitiveColor
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.MyPageBanner
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import java.time.LocalDate
enum class MyPageTab {
    Summary,
    Study,
    League,
    Social
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPage(
    navController: NavController,
    onSessionExpired: () -> Unit
) {
    val context = LocalContext.current
    val vm: UserScreenVM = viewModel(factory = UserVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()
    LaunchedEffect(Unit) { vm.load() }
    val s = (ui as? UserScreenVM.UiState.Success)?.data
    MyPageUI(s?.banners, navController)
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPageUI(
    banner: MyPageBanner?,
    navController: NavController,
) {
    var selectedTab by remember { mutableStateOf(MyPageTab.Summary) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg2)
            .padding(WindowInsets.statusBars.asPaddingValues()),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            MyPageProfileHeader(banner,navController)
        }
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                MyPageTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.padding(top = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                when (selectedTab) {
                    MyPageTab.Summary -> SummaryUI()
                    MyPageTab.Study -> StudyTabUI()
                    MyPageTab.League -> LeagueTabUI()
                    MyPageTab.Social -> SocialTabUI()
                }
            }
        }
    }
}
@Composable
fun MyPageProfileHeader(
    banner: MyPageBanner?,
    navController: NavController,
) {
    Box (
        modifier = Modifier.fillMaxWidth().height(195.dp)
    ){
        Image(
            painter = painterResource(id = R.drawable.mypage_bg),
            contentDescription = "main back",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(modifier = Modifier.height(70.dp)) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(ProfilePalette.idToColor(banner?.profileImgNumber?: 0)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_logo),
                        contentDescription = "profile logo",
                        modifier = Modifier.size(32.dp, 40.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.padding(vertical = 7.5.dp)) {
                    Text(
                        text = "${banner?.nickname}",
                        style = AppTypography.Heading2,
                        color = AppColor.text1w
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ProfileBox("LV ${banner?.level}", 42.dp)
                        ProfileBox("${banner?.currentLeague}", 58.dp)
                    }
                }

                Spacer(Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = R.drawable.setting),
                    contentDescription = "setting",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("user/setting")
                        },
                    tint = AppColor.icon_w

                )
            }

            Text(
                text = "@${banner?.handle}",
                style = AppTypography.Label2,
                color = PrimitiveColor.Gray400
            )

            InlineButton(
                "프로필 편집",
                onClick = { navController.navigate("user/account") },
                state = InlineButtonState.Secondary,
                size = InlineButtonSize.S,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
@Composable
fun ProfileBox(
    text: String,
    width: Dp
){
    Box(
        modifier = Modifier
            .size(width, 22.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(PrimitiveColor.Gray500.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.Caption1,
            color = PrimitiveColor.Gray50
        )
    }
}
@Composable
fun MyPageTabRow(
    selectedTab: MyPageTab,
    onTabSelected: (MyPageTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.bg1)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TapButton(
            text = "요약",
            selected = selectedTab == MyPageTab.Summary,
            onClick = {
                onTabSelected(MyPageTab.Summary)
            },
            modifier = Modifier.weight(1f)
        )
        TapButton(
            text = "학습",
            selected = selectedTab == MyPageTab.Study,
            onClick = {
                onTabSelected(MyPageTab.Study)
            },
            modifier = Modifier.weight(1f)
        )

        TapButton(
            text = "리그",
            selected = selectedTab == MyPageTab.League,
            onClick = {
                onTabSelected(MyPageTab.League)
            },
            modifier = Modifier.weight(1f)
        )

        TapButton(
            text = "소셜",
            selected = selectedTab == MyPageTab.Social,
            onClick = {
                onTabSelected(MyPageTab.Social)
            },
            modifier = Modifier.weight(1f)
        )
    }
}
@Composable
fun TapButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean = true,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onClick,
        modifier = modifier
            .height(36.dp),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if(selected) AppColor.Main2 else AppColor.bg1,
            contentColor = if(selected) AppColor.text1w else AppColor.text3
        )
    ) {
        Text(
            text = text,
            style = AppTypography.Label1
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SummaryUI(

){
    val currentMonth = LocalDate.now().monthValue
    var isFirstHalf by remember { mutableStateOf(currentMonth <= 6) }
    val months =
        if (isFirstHalf) {
            (1..6).toList()
        } else {
            (7..12).toList()
        }
    val colorCube = listOf(AppColor.bg1, PrimitiveColor.Purple200, PrimitiveColor.Purple300,PrimitiveColor.Purple500,PrimitiveColor.Purple700)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimitiveColor.Purple50),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.crown),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp, 18.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "상위 4%",
                            style = AppTypography.Title3,
                            color = AppColor.Main1
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "전체 학습 순위",
                            style = AppTypography.Caption1,
                            color = AppColor.text4
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
                val rankinfo =
                    listOf("145위", "현재 시즌 순위", "14회", "3위권 진입", "브론즈III", "최고티어").chunked(2)
                RankRow(rankinfo)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "학습기록",
                    style = AppTypography.Label2,
                    color = AppColor.text4
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "2025년 상반기",
                        style = AppTypography.Headline2,
                        color = AppColor.text1
                    )
                    Spacer(Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.chevron_left),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = { isFirstHalf = true })
                    )
                    Spacer(Modifier.width(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.chevron_right),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(onClick = { isFirstHalf = false })
                    )
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), 1.dp, AppColor.divider1)
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    months.forEach { month ->
                        Text(
                            text = "${month}월",
                            style = AppTypography.Caption1,
                            color = AppColor.text4
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "적음",
                        style = AppTypography.Caption1,
                        color = AppColor.text3
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        colorCube.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                        }
                    }
                    Text(
                        text = "많음",
                        style = AppTypography.Caption1,
                        color = AppColor.text3
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, AppColor.bg3, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                )
                {
                    Text(
                        text = "임시",
                        style = AppTypography.Caption1,
                        color = AppColor.text3
                    )
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyTabUI() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
                .padding(16.dp),
        ){
            Column {
                Text(
                    text = "리포트",
                    textAlign = TextAlign.Start,
                    style = AppTypography.Label2,
                    color = AppColor.text3
                )
                Spacer(Modifier.height(12.dp))
                ReportBox("일별 완료한 레슨 수", {}, R.drawable.calendar)
                ReportBox("이번주 가장 많이 푼 챕터", {}, R.drawable.fire2)
                ReportBox("취약 개념 TOP 7", {}, R.drawable.trophy)
            }
        }
    }
}
@Composable
fun ReportBox(
    text: String,
    onClick: () -> Unit,
    image: Int
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        verticalAlignment = Alignment.CenterVertically){
        Image(painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(38.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = AppTypography.Label1,
            color = AppColor.text1
        )
        Spacer(Modifier.weight(1f))
        Image(painter = painterResource(id = R.drawable.chevron_right),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onClick))
    }
}
@Composable
fun RankRow(
    rankInfo: List<List<String>>,
    isLeague: Boolean = false
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        rankInfo.forEachIndexed { index, item ->

            RankInfo(
                value = item[0],
                label = item[1],
                modifier = Modifier.weight(1f),
                color = if(index==0 && isLeague) AppColor.Main1 else AppColor.text1
            )

            if (index != rankInfo.lastIndex) {

                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    thickness = 1.dp,
                    color = AppColor.divider1
                )
            }
        }
    }
}
@Composable
fun RankInfo(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = AppColor.text1
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = AppTypography.Headline2,
            color = color
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            style = AppTypography.Caption1,
            color = AppColor.text4
        )
    }
}
@Composable
fun LeagueTabUI() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    Text(
                        text = "리그 시즌 히스토리",
                        style = AppTypography.Label2,
                        color = AppColor.text4
                    )
                    Text(
                        text = "시즌별 최종 티어 기록",
                        style = AppTypography.Headline2,
                        color = AppColor.text1
                    )
                }
                val rankinfo = listOf("145위", "현재 시즌 순위", "14회", "3위권 진입", "브론즈III", "최고티어").chunked(2)
                RankRow(rankinfo, true)
                val tiers = listOf("B1", "B3", "B2", "B2", "B3")

                TierChart(
                    tiers = tiers,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(16.dp)
                )

            }


        }
    }
}
@Composable
fun TierChart(
    tiers: List<String>,
    modifier: Modifier = Modifier
) {
    val tierMap = mapOf(
        "B1" to 1,
        "B2" to 2,
        "B3" to 3
    )

    Canvas(modifier = modifier) {
        val xGap = size.width / (tiers.size - 1)
        val yGap = size.height / 3f

        val points = tiers.mapIndexed { index, tier ->
            val x = index * xGap
            val y = size.height - (tierMap[tier]!! * yGap)

            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)

            points.drop(1).forEach {
                lineTo(it.x, it.y)
            }
        }

        val fillPath = Path().apply {
            addPath(path)
            lineTo(points.last().x, size.height)
            lineTo(points.first().x, size.height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Magenta.copy(alpha = 0.4f),
                    Color.Transparent
                )
            )
        )

        drawPath(
            path = path,
            color = Color.Magenta,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        points.forEach { point ->
            drawCircle(
                color = Color.Magenta,
                radius = 6.dp.toPx(),
                center = point
            )
        }
    }
}
@Composable
fun SocialTabUI() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0),
            contentAlignment = Alignment.Center
        ) {
            Column() {
                val socialInfo = listOf("34", "팔로우", "34", "팔로잉").chunked(2)
                RankRow(socialInfo)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "친구 활동",
                    style = AppTypography.Label2,
                    color = PrimitiveColor.Gray500
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "팔로잉한 친구들의 최근 성취",
                    style = AppTypography.Headline2,
                    color = PrimitiveColor.Gray900
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AppColor.bg0)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "추천 친구",
                    style = AppTypography.Label2,
                    color = PrimitiveColor.Gray500
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "비슷한 레벨의 학습자들",
                    style = AppTypography.Headline2,
                    color = PrimitiveColor.Gray900
                )
            }
        }
    }
}
//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun MyPagePreview() {
//    MyPageUI()
//}