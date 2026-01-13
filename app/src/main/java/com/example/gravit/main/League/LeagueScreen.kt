package com.inuappcenter.gravit.main.League

import android.R.attr.bottom
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import com.inuappcenter.gravit.api.LastSeasonPopupDto
import com.inuappcenter.gravit.api.LeagueItem
import com.inuappcenter.gravit.api.MyLeague
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.SeasonPopupResponse
import com.inuappcenter.gravit.main.Home.LeagueGauge
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import com.inuappcenter.gravit.ui.theme.TierPalette
import com.inuappcenter.gravit.ui.theme.mbc1961
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R
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

    val ui by vm.state.collectAsState()
    val myLeagueState by vm.myLeague.collectAsState()
    val seasonState by vm.seasonPopup.collectAsState()
    val checkLeague by vm.checkLeague.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyLeague()
        vm.selectUserLeague()
        vm.loadSeasonPopup()
    }

    val sessionExpired by vm.sessionExpired.collectAsState()
    val notFound by vm.notFound.collectAsState()

    var navigated by remember { mutableStateOf(false) }
    //세션 만료
    val navTarget: String? = when {
        sessionExpired -> "401"
        notFound -> "404"

        seasonState is LeagueViewModel.SeasonPopupState.SessionExpired -> "401"
        seasonState is LeagueViewModel.SeasonPopupState.NotFound      -> "404"
        seasonState is LeagueViewModel.SeasonPopupState.Failed        -> "FAILED"

        myLeagueState is LeagueViewModel.MyLeagueState.SessionExpired -> "401"
        myLeagueState is LeagueViewModel.MyLeagueState.NotFound       -> "404"
        myLeagueState is LeagueViewModel.MyLeagueState.Failed         -> "FAILED"

        else -> null
    }

    LaunchedEffect(navTarget) {
        if (navTarget == null || navigated) return@LaunchedEffect
        navigated = true
        when (navTarget) {
            "401" -> {
                navController.navigate("error/401") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            "404" -> {
                navController.navigate("error/404") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
            "FAILED" -> {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    when{
        myLeagueState is LeagueViewModel.MyLeagueState.Loading ||
                seasonState   is LeagueViewModel.SeasonPopupState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        myLeagueState is LeagueViewModel.MyLeagueState.Success &&
                seasonState   is LeagueViewModel.SeasonPopupState.Ready -> {
            val listState = rememberLazyListState()

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
            val my = (myLeagueState as LeagueViewModel.MyLeagueState.Success).data
            val season = (seasonState as LeagueViewModel.SeasonPopupState.Ready).data

            LeagueUI(my, vm, checkLeague, season, listState, ui, seasonState)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LeagueUI(
    my: MyLeague,
    vm: LeagueViewModel,
    checkLeague: Boolean,
    season: SeasonPopupResponse,
    listState: LazyListState,
    ui: LeagueViewModel.PagingUi,
    seasonState: LeagueViewModel.SeasonPopupState
){
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            my.let { rank -> //내 랭킹
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
                                    .height(35.dp),
                            ){
                                Image(
                                    painter = TierPalette.painterFor(rank.leagueId),
                                    contentDescription = "tier",
                                    modifier = Modifier.height(30.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = rank.nickname,
                                    modifier = Modifier.align(Alignment.Bottom),
                                    style = TextStyle(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = mbc1961,
                                        color = Color.Black,
                                    )
                                )
                            }
                        }
                    }
                }
            }
            val seasonName = season.currentSeason.nowSeason
            if(checkLeague){
                SeasonFinish(seasonName)
            }else{
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = seasonName,
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
                        TierSelector(vm = vm, initialLeagueId = my.leagueId)
                        Spacer(Modifier.height(20.dp))
                    }
                }

                HorizontalDivider(
                    color = Color(0xFFDCDCDC),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
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
                                    Text(
                                        text = "더이상 유저가 없습니다.",
                                        fontFamily = pretendard,
                                        color = Color.Black
                                    )
                                }
                            }
                            ui.error != null -> {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(ui.error)
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
    val ready = seasonState as? LeagueViewModel.SeasonPopupState.Ready
    val seasonPopup = ready?.data?.lastSeasonPopupDto
    if (ready?.show == true && seasonPopup != null) {
        SeasonCompleted(
            popupDetail = seasonPopup,
            onConfirm = { vm.confirmSeasonPopup(ready.data.currentSeason.nowSeason) }
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
                                .background(ProfilePalette.idToColor(popupDetail.profileImgNumber)),
                            contentAlignment = Alignment.Center
                        ){
                            Image(
                                painter = painterResource(id = R.drawable.profile_logo),
                                contentDescription = "profile logo",
                                modifier = Modifier.size(64.dp, 82.dp)
                            )
                        }
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
                    modifier = Modifier.size(175.dp, 56.dp),
                    verticalAlignment = Alignment.CenterVertically
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
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .background(Color(0xFFFFFFFF), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFDCDCDC), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
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
                modifier = Modifier
                    .width(50.dp)
                    .padding(top = 1.dp),
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
fun TierSelector(
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

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { moving ->
            if (moving) {
                userHasScrolled = true
            } else if (userHasScrolled && centerIndex != lastAppliedIndex) {
                val leagueId = tiers.getOrNull(centerIndex) ?: return@collect
                vm.selectTier(leagueId)
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
private fun tierName(id: Int): String = when (id) {
    1 -> "브론즈 3"
    2 -> "브론즈 2"
    3 -> "브론즈 1"
    4 -> "실버 3"
    5 -> "실버 2"
    6 -> "실버 1"
    7 -> "골드 3"
    8 -> "골드 2"
    9 -> "골드 1"
    10 -> "플래티넘 3"
    11 -> "플래티넘 2"
    12 -> "플래티넘 1"
    13 -> "다이아몬드 3"
    14 -> "다이아몬드 2"
    15 -> "다이아몬드 1"
    else -> "Unranked"
}

private fun tierIdFromName(name: String?): Int = when (name) {
    "Bronze 3" -> 1
    "Bronze 2" -> 2
    "Bronze 1" -> 3
    "Silver 3" -> 4
    "Silver 2" -> 5
    "Silver 1" -> 6
    "Gold 3" -> 7
    "Gold 2" -> 8
    "Gold 1" -> 9
    "Platinum 3" -> 10
    "Platinum 2" -> 11
    "Platinum 1" -> 12
    "Diamond 3" -> 13
    "Diamond 2" -> 14
    "Diamond 1" -> 15
    else -> -1
}

fun TextColor(tierId: Int) : Color =
    when(tierId) {
        3, 2 ,1 -> Color(0xFF6C3F00)
        6, 5, 4 -> Color(0xFF818181)
        9, 8, 7 -> Color(0xFFE29F00)
        12, 11, 10 -> Color(0xFF00B399)
        15, 14, 13 -> Color(0xFF00AFC3)
    else -> Color(0xFF6C3F00)

}
@Composable
private fun TierDot(
    tierId: Int,
    selected: Boolean
) {
    val size = if (selected) 107.dp else 80.dp
    val label = tierName(tierId)

    val boxHeight = 107.dp

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
            modifier = Modifier
                .height(boxHeight)
                .width(size + 10.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (selected) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pretendard,
                    color = TextColor(tierId),
                )
            )
        }
    }
}
