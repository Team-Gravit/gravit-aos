package com.example.gravit.main.User

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.R
import com.example.gravit.api.FriendItem
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.FollowTab
import com.example.gravit.ui.theme.ProfilePalette
import com.example.gravit.ui.theme.pretendard

@Composable
fun FollowList(
    navController: NavController,
    initialTab: FollowTab
){
    var tab by rememberSaveable { mutableStateOf(initialTab) }


    val context = LocalContext.current
    val vm: FollowViewModel = viewModel(
        factory = FollowVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()
    val followerCount by vm.followerCount.collectAsState()
    val followingCount by vm.followingCount.collectAsState()

    LaunchedEffect(tab) {
        when(tab) {
            FollowTab.Followers -> vm.loadFollower()
            FollowTab.Following -> vm.loadFollowing()
        }
    }
    when (ui) {
        FollowViewModel.UiState.SessionExpired -> {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        }

        else -> Unit
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(WindowInsets.statusBars.asPaddingValues())
        .background(Color.White)
    ){
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = "back",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(24.dp)
                            .clickable { navController.popBackStack() },
                        colorFilter = ColorFilter.tint(color = Color.Black)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "팔로우/팔로잉",
                        fontSize = 16.sp,
                        fontFamily = pretendard,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF222222)
                    )
                }

            }
            FollowTabBar(
                tab = tab,
                onTabChange = { newTab -> tab = newTab },
                followerCount = followerCount,
                followingCount = followingCount
            )

            when (ui) {
                is FollowViewModel.UiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is FollowViewModel.UiState.Success -> {
                    val data = (ui as FollowViewModel.UiState.Success).data
                    val follow = data.size
                    FriendList(data)
                }

                is FollowViewModel.UiState.Failed -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        //TODO 나중에
                    }
                }
                else -> Unit

            }
        }
    }
}

@Composable
fun FriendList(users: List<FriendItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(users) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ProfilePalette.idToColor(user.profileImgNumber)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_logo),
                        contentDescription = "profile logo",
                        modifier = Modifier.size(20.dp)
                    )
                }
                // 프로필 이미지
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "@${user.nickname}",
                        fontWeight = FontWeight.Medium,
                        fontFamily = pretendard,
                        fontSize = 14.sp,
                        color = Color(0xFF222222)
                    )
                    Text(
                        text = "@${user.handle}",
                        fontWeight = FontWeight.Medium,
                        fontFamily = pretendard,
                        fontSize = 14.sp,
                        color = Color(0xFF222222)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowTabBar(
    tab: FollowTab,
    onTabChange: (FollowTab) -> Unit,
    followerCount: Int,
    followingCount: Int

) {
    PrimaryTabRow(
        selectedTabIndex = tab.ordinal,
        containerColor = Color.White,
        indicator = {
            Box(
                Modifier
                    .tabIndicatorOffset(tab.ordinal, matchContentSize = false)
                    .height(2.dp)
                    .background(Color.Black)
            )
        }
    ) {
        Tab(
            selected = tab == FollowTab.Followers,
            onClick = { onTabChange(FollowTab.Followers) },
            modifier = Modifier.height(41.dp)
        ) {
            Text(
                text = "${followerCount} 팔로워",
                fontSize = 14.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.SemiBold,
                color = if (tab == FollowTab.Followers) Color.Black else Color(0xFFCCCCCC)
            )
        }

        Tab(
            selected = tab == FollowTab.Following,
            onClick = { onTabChange(FollowTab.Following) },
            modifier = Modifier.height(41.dp)
        ) {
            Text(
                text = "${followingCount} 팔로잉",
                fontSize = 14.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.SemiBold,
                color = if (tab == FollowTab.Following) Color.Black else Color(0xFFCCCCCC)
            )
        }
    }
}

