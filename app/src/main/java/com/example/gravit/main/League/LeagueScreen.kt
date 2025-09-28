package com.example.gravit.main.League

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.LastSeasonPopupDto
import com.example.gravit.api.LeagueItem
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Home.LeagueGauge
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.TierPalette
import com.example.gravit.ui.theme.gmarketsans
import com.example.gravit.ui.theme.mbc1961
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LeagueScreen(
    navController: NavController,
    onSessionExpired: () -> Unit
) {
    val context = LocalContext.current
    val vm: LeagueViewModel = viewModel(factory = LeagueVMFactory(RetrofitInstance.api, context))
    val source by vm.source.collectAsState()
    val ui by vm.state.collectAsState()
    val myLeagueState by vm.myLeague.collectAsState()
    val seasonState by vm.seasonPopup.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyLeague()
        vm.selectUserLeague()
        vm.loadSeasonPopup()
    }

    val sessionExpired by vm.sessionExpired.collectAsState()
    val notFound by vm.notFound.collectAsState()
    var navigated by remember { mutableStateOf(false) }
    //세션 만료
    LaunchedEffect(sessionExpired) {
        if (sessionExpired) {
            navigated = true
            navController.navigate("error/401") {
                popUpTo(0); launchSingleTop = true; restoreState = false
            }
        }
    }
    LaunchedEffect(notFound) {
        if (notFound) {
            navigated = true
            navController.navigate("error/404") {
                popUpTo(0); launchSingleTop = true; restoreState = false
            }
        }
    }

    LaunchedEffect(seasonState) {
        when (seasonState) {
            LeagueViewModel.SeasonPopupState.SessionExpired ->  {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            LeagueViewModel.SeasonPopupState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            LeagueViewModel.SeasonPopupState.Failed -> {
                navigated = true
                onSessionExpired()
            }
            else -> Unit
        }
    }
    LaunchedEffect(myLeagueState) {
        when (myLeagueState) {
            LeagueViewModel.MyLeagueState.SessionExpired ->  {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            LeagueViewModel.MyLeagueState.NotFound -> {
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            LeagueViewModel.MyLeagueState.Failed -> {
                navigated = true
                onSessionExpired()
            }
            else -> Unit
        }
    }

    val listState = rememberLazyListState()

    // 리스트 끝 근처에서 다음 페이지 요청
    LaunchedEffect(listState, ui.items.size) {
        snapshotFlow {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val total = listState.layoutInfo.totalItemsCount
            if (last != null) last to total else null
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (lastVisible >= total - 3) vm.loadNextL()
            }
    }
    val my = (myLeagueState as? LeagueViewModel.MyLeagueState.Success)?.data
    val listLeagueId: Int = when (val s = source) {
        is LeagueViewModel.Source.Tier -> s.leagueId
        LeagueViewModel.Source.UserLeague -> my?.leagueName?.let { tierIdFromName(it) } ?: -1
    }
    val myLeagueId = my?.leagueId
    val myLeagueNmae = my?.leagueName?.let {tierIdFromName(it)}
    val season = (seasonState as? LeagueViewModel.SeasonPopupState.Ready)?.data

    val shadow = with(LocalDensity.current) {
        Shadow(
            color = Color.Black.copy(alpha = 0.25f),
            offset = Offset(0f, 1.46.dp.toPx()),
            blurRadius = 1.46.dp.toPx()
        )
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(WindowInsets.statusBars.asPaddingValues())
        .background(Color(0xFFF2F2F2)))
    {
        Column(modifier = Modifier.fillMaxSize()) {
            my?.let { rank -> //내 랭킹
                Surface(
                    shadowElevation = 5.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(
                                    color = Color(0xFFFFB608),
                                    fontFamily = mbc1961,
                                    fontSize = 20.sp
                                )){
                                    append("${rank.rank}")
                                }
                                withStyle(SpanStyle(
                                    color = Color.Black,
                                    fontSize = 18.sp,
                                    fontFamily = pretendard,
                                    )){
                                    append("등")
                                }
                            },
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box (modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center){
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(ProfilePalette.idToColor(rank.profileImgNumber)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_logo),
                                    contentDescription = "profile logo",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            LeagueGauge(
                                xp = rank.xp,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row {
                                Text(
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(
                                            color = Color(0xFF5A5A5A))){
                                            append("LV")
                                        }
                                        append(" ")
                                        withStyle(SpanStyle(color = Color(0xFFFF9500))){
                                            append("${rank.level}")
                                        }
                                    },
                                    style = TextStyle(
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = pretendard,
                                        fontSize = 12.sp,
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    )
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color(0xFF5A5A5A))){
                                            append("LP")
                                        }
                                        append(" ")
                                        withStyle(SpanStyle(color = Color(0xFFFF9500))){
                                            append("${rank.lp}")
                                        }
                                        append(" ")
                                        withStyle(SpanStyle(color = Color(0xFF5A5A5A))){
                                            append("/ ${rank.maxLp}")
                                        }
                                    },
                                    style = TextStyle(
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = pretendard,
                                        fontSize = 12.sp,
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    )
                                )
                            }

                            Row (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                            ){
                                Image(
                                    painter = TierPalette.painterFor(rank.leagueId),
                                    contentDescription = "tier",
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = rank.nickname,
                                    style = TextStyle(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = mbc1961,
                                        color = Color.Black,
                                    )
                                )
                            }
                        }
                    }
                }
            }

            //티어 선택
            Box(
                contentAlignment = Alignment.Center
            ) {
                val seasonName = season?.currentSeason?.nowSeason
                Column {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "${seasonName}",
                        color = Color(0xFF8A00B8),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontFamily = mbc1961,
                            fontSize = 24.sp,
                            shadow = shadow
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(192.dp, 28.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ){
                        Row (modifier = Modifier.align(Alignment.CenterStart)) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_timer_24),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .align(Alignment.CenterVertically)
                                    .size(15.dp),
                                tint = Color.Black
                            )
                            Spacer(Modifier.width(10.dp))
                            WeeklyCountdown()
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    TierSelector(vm = vm, initialLeagueId = myLeagueId)
                    Spacer(Modifier.height(20.dp))
                }
            }

            HorizontalDivider(
                color = Color(0xFFDCDCDC),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            //랭킹
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ui.items, key = { it.userId }) { item ->
                        RankCell(item = item)
                    }

                    item {
                        when {
                            ui.isLoading -> {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            ui.endReached -> {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("마지막입니다.")
                                }
                            }
                            ui.error != null -> {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(ui.error!!)
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedButton(onClick = { vm.loadNextL() }) { Text("다시 시도") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    val seasonPopup = season?.lastSeasonPopupDto
    if (season?.containsPopup == true && seasonPopup != null) {
        SeasonCompleted(
            popupDetail = seasonPopup,
            onConfirm = { vm.confirmSeasonPopup(season.currentSeason.nowSeason) }
        )
    }
}

@Composable
fun SeasonCompleted(
    popupDetail: LastSeasonPopupDto,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(497.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(30.dp))
                Text(
                    text = "시즌 종료",
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(22.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val shadow = with(LocalDensity.current) {
                        Shadow(
                            color = Color.Black.copy(alpha = 0.25f),
                            offset = Offset(0f, 1.46.dp.toPx()),
                            blurRadius = 1.46.dp.toPx()
                        )
                    }
                    Text(
                        text = "${popupDetail.rank}등",
                        color = Color(0xFF8100B3),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            shadow = shadow
                        ),
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.leaf),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 35.dp, top = 20.dp)
                            .size(81.dp, 99.dp)
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 50.dp)
                            .size(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                                .background(ProfilePalette.idToColor(popupDetail.profileImgNumber))
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .border(
                                    width = 5.dp,
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF8100B3), Color(0xFFDD00FF))
                                    ),
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.leaf),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 20.dp, end = 35.dp)
                            .size(81.dp, 99.dp)
                            .graphicsLayer { scaleX = -1f }
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row (
                    modifier = Modifier.size(175.dp, 56.dp)
                ){
                    Image(
                        painter = TierPalette.painterFor(tierIdFromName(popupDetail.leagueName)),
                        contentDescription = "tier"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = popupDetail.leagueName,
                        fontSize = 32.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222124)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "다음 시즌도 지금처럼\n진행해주세요!",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    fontFamily = pretendard,
                    color = Color(0xFF6D6D6D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(275.dp, 53.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8100B3))
                ) {
                    Text(
                        text = "확인",
                        fontSize = 18.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
@Composable
private fun RankCell(
    item: LeagueItem
) { //랭킹
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(54.dp)
            .border(1.dp, Color(0xFFDCDCDC), RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFFFF), RoundedCornerShape(16.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(3f)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.rank.toString().padStart(3, '0'),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = mbc1961,
                    color = if(item.rank == 1 || item.rank == 2 || item.rank == 3) Color(0xFFBA00FF) else Color(0xFFFFB608),
                    fontFeatureSettings = "tnum"
                ),
                modifier = Modifier.width(50.dp),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ProfilePalette.idToColor(item.profileImgNumber)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_logo),
                        contentDescription = "profile logo",
                        modifier = Modifier.size(15.dp)
                    )
                }
                LeagueGauge(
                    xp = item.xp,
                    modifier = Modifier.fillMaxSize()
                )

            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = item.nickname,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = mbc1961,
                color = Color.Black
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(0xFFFFF2FF))
                .padding(horizontal = 15.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF4C4C4C))){
                        append("LV")
                    }
                    append(" ")

                    withStyle(SpanStyle(color = Color(0xFFFF9500))){
                        append("${item.level}")
                    }
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                modifier = Modifier.align(Alignment.Start),
            )
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF4C4C4C))){
                        append("LP")
                    }
                    append(" ")

                    withStyle(SpanStyle(color = Color(0xFFFF9500))){
                        append("${item.lp}")
                    }
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = pretendard,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                modifier = Modifier.align(Alignment.Start),
            )
        }
    }
}

@Composable
fun TierSelector( //티어 선택
    vm: LeagueViewModel,
    tiers: List<Int> = (1..15).toList(),
    dotSize: Dp = 100.dp,
    spacing: Dp = 12.dp,
    initialLeagueId: Int?
) {
    val listState = rememberLazyListState()
    val fling = rememberSnapFlingBehavior(listState)

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val sidePad = (screenWidth - dotSize) / 2

    //중앙에 가장 가까운 아이템 계산
    val centerIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val vpCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2
            info.visibleItemsInfo.minByOrNull { item ->
                val center = item.offset + item.size / 2
                abs(center - vpCenter)
            }?.index ?: 0
        }
    }

    var userHasScrolled by remember { mutableStateOf(false) }
    var lastAppliedIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(initialLeagueId, tiers) {
        val idx = initialLeagueId?.let { tiers.indexOf(it) } ?: -1
        if (idx >= 0) {
            listState.scrollToItem(idx)
            lastAppliedIndex = idx
            vm.selectTier(tiers[idx])
        }
    }

    // 스크롤이 멈췄을 때만 가운데 티어로 전환
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { moving ->
            if (moving) {
                userHasScrolled = true
            } else if (userHasScrolled && centerIndex != lastAppliedIndex) {
                val leagueId = tiers.getOrNull(centerIndex) ?: return@collect
                vm.selectTier(leagueId)      //소스 전환 + 0페이지부터 재로딩
                lastAppliedIndex = centerIndex
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        LazyRow(
            state = listState,
            flingBehavior = fling,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            contentPadding = PaddingValues(horizontal = sidePad),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(tiers, key = { idx, id -> "tier-$id-$idx" }) { idx, id ->
                val selected = idx == centerIndex
                TierDot(
                    tierId = id,
                    selected = selected,
                )
            }
        }
    }
}
private fun tierName(id: Int): String = when (id) { //티어 이름 변경
    1 -> "브론즈 1"
    2 -> "브론즈 2"
    3 -> "브론즈 3"
    4 -> "실버 1"
    5 -> "실버 2"
    6 -> "실버 3"
    7 -> "골드 1"
    8 -> "골드 2"
    9 -> "골드 3"
    10 -> "플래티넘 1"
    11 -> "플래티넘 2"
    12 -> "플래티넘 3"
    13 -> "다이아몬드 1"
    14 -> "다이아몬드 2"
    15 -> "다이아몬드 3"
    else -> "Unranked"
}

private fun tierIdFromName(name: String?): Int = when (name) {
    "Bronze 1" -> 1
    "Bronze 2" -> 2
    "Bronze 3" -> 3
    "Silver 1" -> 4
    "Silver 2" -> 5
    "Silver 3" -> 6
    "Gold 1" -> 7
    "Gold 2" -> 8
    "Gold 3" -> 9
    "Platinum 1" -> 10
    "Platinum 2" -> 11
    "Platinum 3" -> 12
    "Diamond 1" -> 13
    "Diamond 2" -> 14
    "Diamond 3" -> 15
    else -> -1 // Unranked나 매칭 안 될 때
}
@Composable
private fun TierDot( //티어 (아직 로고 안 넣음)
    tierId: Int,
    selected: Boolean
) {
    val size = if (selected) 107.dp else 80.dp   // 중앙은 더 크게
    val label = tierName(tierId)
    val darkenFilter = ColorFilter.colorMatrix(
        ColorMatrix(
            floatArrayOf(
                0.6f, 0f, 0f, 0f, 0f,
                0f, 0.6f, 0f, 0f, 0f,
                0f, 0f, 0.6f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(size+10.dp),
        ) {
            if(selected){
                Column {
                    Image(
                        painter = TierPalette.painterFor(tierId),
                        contentDescription = "tier",
                        modifier = Modifier.size(size)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.shadow),
                        contentDescription = null,
                        modifier = Modifier.size(size)
                    )
                }
             } else {
                Image(
                    painter = TierPalette.painterFor(tierId),
                    contentDescription = "tier",
                    modifier = Modifier.size(size),
                    colorFilter = darkenFilter
                )
            }
        }
        Spacer(Modifier.height(7.dp))
        if (selected) {
            val density = LocalDensity.current
            val shadow = with(density) {
                Shadow(
                    Color.Black.copy(alpha = 0.25f),
                    offset = Offset(0f, (1.46).dp.toPx()),
                    blurRadius = 10.dp.toPx()
                )
            }
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = gmarketsans,
                    color = Color(0xFF8A00B8),
                    shadow = shadow

                )
            )
        }
    }
}