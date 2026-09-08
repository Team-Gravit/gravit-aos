package com.inuappcenter.gravit.main.League

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.example.gravit.ui.theme.PrimitiveColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.api.LeagueItem
import com.inuappcenter.gravit.api.MyLeague
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.api.SeasonPopupResponse
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import com.inuappcenter.gravit.ui.theme.TierPalette
import com.inuappcenter.gravit.ui.theme.mbc1961
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
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
    val hazeState = rememberHazeState()
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = false
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColor.bg1)
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.league_back),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .hazeSource(hazeState),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues())
                ){
                    val seasonName = season.currentSeason.nowSeason
                    if(checkLeague){
                        SeasonFinish()
                    }else{
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                Spacer(Modifier.height(40.dp))
                                Text(
                                    text = seasonName,
                                    color = AppColor.text1w,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = TextStyle(
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = mbc1961,
                                        fontSize = 22.sp
                                    )
                                )
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .size(150.dp, 33.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(AppColor.bg0),
                                    contentAlignment = Alignment.Center
                                ){
                                    WeeklyCountdown()
                                }
                                Spacer(Modifier.height(24.dp))
                                TierSelector(vm = vm, initialLeagueId = my.leagueId)
                                Spacer(Modifier.height(24.dp))
                            }
                        }

                        HorizontalDivider(
                            color = AppColor.divider2,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(24.dp))
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            itemsIndexed(
                                items = ui.items,
                                key = { index, item ->
                                    "${item.userId ?: "unknown"}$index"
                                }
                            ) { _, item ->
                                RankCell(item = item, hazeState)
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
                                                color = AppColor.text2w
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
            val seasonPopup = ready?.data
            if (ready?.show == true && seasonPopup != null) {
                SeasonCompleted(
                    popupDetail = seasonPopup,
                    onConfirm = { vm.confirmSeasonPopup(ready.data.currentSeason.nowSeason) },
                    lp = my.lp
                )
            }
        }
    }
}

@Composable
fun SeasonCompleted(
    popupDetail: SeasonPopupResponse,
    onConfirm: () -> Unit,
    lp: Int
) {
    var show = false
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
                .height(597.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "${popupDetail.currentSeason.nowSeason} 종료!",
                    style = AppTypography.Title3,
                    color = AppColor.Main1
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "이번 시즌도 수고했어요.",
                    style = AppTypography.Label2,
                    color = AppColor.text4
                )
                Spacer(Modifier.height(50.dp))
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = painterResource(id = R.drawable.sparkle),
                        contentDescription = null,
                        modifier = Modifier
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = TierPalette.painterFor(tierIdFromName(popupDetail.lastSeasonPopupDto?.leagueName)),
                            contentDescription = "tier",
                            modifier = Modifier.size(140.dp, 182.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "최종 티어",
                            style = AppTypography.Label2,
                            color = AppColor.text4
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = popupDetail.lastSeasonPopupDto?.leagueName?: "",
                            style = AppTypography.Heading1,
                            color = AppColor.text1
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .size(142.dp, 71.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimitiveColor.Gray200),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = lp.toString(),
                                style = AppTypography.Headline2,
                                color = AppColor.text2
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "최종 LP",
                                style = AppTypography.Caption1,
                                color = AppColor.text3w
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .size(142.dp, 71.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimitiveColor.Gray200),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(
                                text = popupDetail.lastSeasonPopupDto?.rank
                                    ?.let { "${it}위" }
                                    ?: "-",
                                style = AppTypography.Headline2,
                                color = AppColor.text2
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "최종 순위",
                                style = AppTypography.Caption1,
                                color = AppColor.text3w
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                BlockButton(
                    text = "다음으로",
                    state = ButtonState.Default,
                    onClick = { show = true },
                    modifier = Modifier.height(47.dp)
                )
            }
        }
    }
    if(show)
        SeasonStart(popupDetail, onConfirm)
}
@Composable
fun SeasonStart(
    popupDetail: SeasonPopupResponse,
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
                .height(597.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "${popupDetail.currentSeason.nowSeason} 시작!",
                    style = AppTypography.Title3,
                    color = AppColor.Main1
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "직전 티어 기준으로 시작 위치가 정해져요!",
                    style = AppTypography.Label2,
                    color = AppColor.text4
                )
                Spacer(Modifier.height(50.dp))
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = painterResource(id = R.drawable.sparkle),
                        contentDescription = null,
                        modifier = Modifier
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = TierPalette.painterFor(tierIdFromName(popupDetail.lastSeasonPopupDto?.nextLeagueName)),
                            contentDescription = "tier",
                            modifier = Modifier.size(140.dp, 182.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "시작 티어",
                            style = AppTypography.Label2,
                            color = AppColor.text4
                        )
                        Spacer(Modifier.height(8.dp))
                        Row {
                            Text(
                                text = popupDetail.lastSeasonPopupDto?.leagueName ?: "",
                                style = AppTypography.Label1,
                                color = AppColor.text4
                            )
                            Text(
                                text = "->${popupDetail.lastSeasonPopupDto?.nextLeagueName}",
                                style = AppTypography.Heading1,
                                color = AppColor.text1
                            )
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(71.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimitiveColor.Gray200),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = popupDetail.lastSeasonPopupDto?.nextStartLp
                                    ?.toString()
                                    ?: "-",
                                style = AppTypography.Headline2,
                                color = AppColor.text2
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "시작 LP",
                                style = AppTypography.Caption1,
                                color = AppColor.text3w
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                BlockButton(
                    text = "다음으로",
                    state = ButtonState.Default,
                    onClick = onConfirm,
                    modifier = Modifier.height(47.dp)
                )
            }
        }
    }
}
@OptIn(ExperimentalHazeApi::class)
@Composable
private fun RankCell(
    item: LeagueItem,
    hazeState: HazeState
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(62.dp)
            .clip(RoundedCornerShape(8.dp))
            .hazeEffect(state = hazeState) {
                backgroundColor =
                    Color(0xFF32147D).copy(alpha = 0.12f)

                tints = listOf(
                    HazeTint(
                        Color(0xFF7040D8).copy(alpha = 0.18f)
                    ),
                    HazeTint(
                        Color(0xFFB58AFF).copy(alpha = 0.05f)
                    ),
                    HazeTint(
                        Color.White.copy(alpha = 0.03f)
                    )
                )

                blurRadius = 5.dp
                noiseFactor = 0.04f
            }
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.7f),
                        Color(0xFFCDB9FF).copy(alpha = 0.28f),
                        Color(0xFFB695FF).copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(64.dp)
                .background(Color(0XFFF4E3FF).copy(alpha = 0.5f)),
           contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.rank
                    ?.toString()
                    ?.padStart(3, '0')
                    ?: "---",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = mbc1961,
                    color = AppColor.CTA_text,
                    fontFeatureSettings = "enum"
                )
            )
        }
        Row(
            modifier = Modifier
                .weight(3.5f)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ProfilePalette.idToColor(item.profileImgNumber ?: 0)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_logo),
                    contentDescription = "profile logo",
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = item.nickname,
                style = AppTypography.Label1,
                color = AppColor.CTA_text
            )
            Spacer(Modifier.weight(1f))
            VerticalDivider(
                modifier = Modifier.fillMaxHeight(),
                1.dp, AppColor.text2w
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(
                        AppTypography.Caption1.toSpanStyle()
                            .copy(color = AppColor.text2w)
                    ) {
                        append("LV")
                    }
                    append(" ")
                    withStyle(
                        AppTypography.Label1.toSpanStyle()
                            .copy(color = PrimitiveColor.Purple300)
                    ) {
                        append(item.level?.toString() ?: "-")
                    }
                },
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                buildAnnotatedString {
                    withStyle(
                        AppTypography.Caption1.toSpanStyle()
                            .copy(color = AppColor.text2w)
                    ) {
                        append("LP")
                    }
                    append(" ")
                    withStyle(
                        AppTypography.Label1.toSpanStyle()
                            .copy(color = PrimitiveColor.Purple300)
                    ) {
                        append(item.lp?.toString() ?: "-")
                    }
                },
                style = TextStyle(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TierSelector(
    vm: LeagueViewModel,
    tiers: List<Long> = (1L..15L).toList(),
    dotSize: Dp = 100.dp,
    spacing: Dp = 12.dp,
    initialLeagueId: Long
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
        val idx = initialLeagueId.let { tiers.indexOf(it) }
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
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
private fun tierName(id: Long): String = when (id) {
    1L -> "브론즈 3"
    2L -> "브론즈 2"
    3L -> "브론즈 1"
    4L -> "실버 3"
    5L -> "실버 2"
    6L -> "실버 1"
    7L -> "골드 3"
    8L -> "골드 2"
    9L -> "골드 1"
    10L -> "플래티넘 3"
    11L -> "플래티넘 2"
    12L -> "플래티넘 1"
    13L -> "다이아몬드 3"
    14L -> "다이아몬드 2"
    15L -> "다이아몬드 1"
    else -> "Unranked"
}

private fun tierIdFromName(name: String?): Long = when (name) {
    "Bronze 3" -> 1L
    "Bronze 2" -> 2L
    "Bronze 1" -> 3L
    "Silver 3" -> 4L
    "Silver 2" -> 5L
    "Silver 1" -> 6L
    "Gold 3" -> 7L
    "Gold 2" -> 8L
    "Gold 1" -> 9L
    "Platinum 3" -> 10L
    "Platinum 2" -> 11L
    "Platinum 1" -> 12L
    "Diamond 3" -> 13L
    "Diamond 2" -> 14L
    "Diamond 1" -> 15L
    else -> -1L
}
@Composable
private fun TierDot(
    tierId: Long,
    selected: Boolean
) {
    val size = if (selected) 107.dp else 80.dp
    val label = tierName(tierId)

    Column(
        modifier = Modifier.width(size),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(size)
                .height(107.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (selected) {
                Image(
                    painter = painterResource(R.drawable.shadow),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(
                        color = Color.Black.copy(alpha = 0.65f),
                        blendMode = BlendMode.SrcIn
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .width(80.dp)
                        .height(7.dp)
                )
            }
            Image(
                painter = TierPalette.painterFor(tierId),
                contentDescription = "tier",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-2).dp)
                    .size(size)
            )

        }
        Spacer(Modifier.height(12.dp))
        if (selected) {
            Text(
                text = label,
                style = AppTypography.Heading2,
                color = AppColor.text1w,
                maxLines = 1
            )
        }
    }
}