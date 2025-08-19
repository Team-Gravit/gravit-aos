package com.example.gravit.main.League

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gravit.R
import com.example.gravit.api.LeagueItem
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.gmarketsans
import com.example.gravit.ui.theme.mbc1961
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.abs

@Composable
fun LeagueScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val vm: LeagueViewModel = viewModel(factory = LeagueVMFactory(RetrofitInstance.api, context))
    val ui by vm.state.collectAsState()
    val myRank by vm.myRank.collectAsState()
    val sessionExpired by vm.sessionExpired.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadMyUser()
        vm.prefetchMyRank()
        vm.selectUserLeague()
    }

    //세션 만료
    LaunchedEffect(sessionExpired) {
        if (sessionExpired) {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
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

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(WindowInsets.statusBars.asPaddingValues())
        .background(Color(0xFFF2F2F2)))
    {
        Column(modifier = Modifier.fillMaxSize()) {
            myRank?.let { rank -> //내 랭킹
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
                                    color = Color(0xFF930000),
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row {
                                Text(
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(
                                            color = Color(0xFFFF3B2F))){
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
                                        withStyle(SpanStyle(
                                            color = Color(0xFFFF3B2F))){
                                            append("EXP")
                                        }
                                        append(" ")
                                        withStyle(SpanStyle(color = Color(0xFFFF9500))){
                                            append("${rank.xp}XP")
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

            //티어 선택
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                TierSelector(vm = vm)
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
                    .weight(5f)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ui.items, key = { it.userId }) { item ->
                        RankCell(item)
                    }

                    item {
                        when {
                            ui.isLoading -> {
                                Box(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            ui.endReached -> {
                                Box(
                                    Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("마지막 페이지입니다")
                                }
                            }
                            ui.error != null -> {
                                Column(
                                    Modifier.fillMaxWidth().padding(16.dp),
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
}
@Composable
private fun RankCell(item: LeagueItem) { //랭킹
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
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
                    color = Color(0xFF930000),
                    fontFeatureSettings = "tnum"
                ),

                modifier = Modifier.width(45.dp),
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ProfilePalette.idToColor(item.profileImgNumber)),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.profile_logo),
                    contentDescription = "profile logo",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = item.nickname,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                color = Color.Black
            )
        }
        Column(
            modifier = Modifier.weight(1f)
                .background(Color(0xFFFFF2FF))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF4C4C4C))){
                        append("LV")
                    }
                    append("  ")

                    withStyle(SpanStyle(color = Color(0xFFFF9500))){
                        append("${item.level}")
                    }
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF4C4C4C))){
                        append("LP")
                    }
                    append("  ")

                    withStyle(SpanStyle(color = Color(0xFFFF9500))){
                        append("${item.lp}")
                    }
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = pretendard,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}

@Composable
fun TierSelector( //티어 선택
    vm: LeagueViewModel,
    tiers: List<Int> = (1..15).toList(),
    dotSize: Dp = 100.dp,
    spacing: Dp = 12.dp
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
    var lastAppliedIndex by remember { mutableStateOf(-1) }

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
                    isCenter = true
                )
            }
        }
    }
}
private fun tierName(id: Int): String = when (id) { //티어 이름 변경
    1 -> "Bronze 1"
    2 -> "Bronze 2"
    3 -> "Bronze 3"
    4 -> "Silver 1"
    5 -> "Silver 2"
    6 -> "Silver 3"
    7 -> "Gold 1"
    8 -> "Gold 2"
    9 -> "Gold 3"
    10 -> "Platinum 1"
    11 -> "Platinum 2"
    12 -> "Platinum 3"
    13 -> "Diamond 1"
    14 -> "Diamond 2"
    15 -> "Diamond 3"
    else -> "Unranked"
}

@Composable
private fun TierDot( //티어 (아직 로고 안 넣음)
    tierId: Int,
    isCenter: Boolean,
    selected: Boolean
) {
    val size = if (isCenter) 107.dp else 80.dp   // 중앙은 더 크게
    val label = tierName(tierId)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(if (selected) Color(0xFFEEE0FF) else Color.White)
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) Color(0xFF7B61FF) else Color(0xFFE1E1E1),
                    shape = CircleShape
                )
        )
        Spacer(Modifier.height(28.dp))
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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = gmarketsans,
                    color = Color(0xFF8A00B8),
                    shadow = shadow

                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeagueScreenPreview() {
    val navController = rememberNavController()
    LeagueScreen(navController)
}