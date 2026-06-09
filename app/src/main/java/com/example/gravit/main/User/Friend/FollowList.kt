package com.inuappcenter.gravit.main.User.Friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.InlineButton
import com.example.gravit.ui.theme.InlineButtonState
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.User.TopBar
import com.inuappcenter.gravit.navigation.FollowTab
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.FriendFollowerItem
import com.inuappcenter.gravit.api.FriendUFollowingItem
import kotlin.collections.isNotEmpty

@Composable
fun FollowList(
    navController: NavController,
    initialTab: FollowTab
) {
    val ctx = LocalContext.current
    val vm: FriendListVM = viewModel(
        factory = FriendListVMFactory(
            api = RetrofitInstance.api,
            appContext = ctx.applicationContext
        )
    )
    val ui by vm.state.collectAsState()

    val vm1: AddFriendVM = viewModel(
        factory = AddFriendVMFactory(
            api = RetrofitInstance.api,
            appContext = ctx.applicationContext
        )
    )
    val ui1 by vm1.state.collectAsState()

    LaunchedEffect(initialTab) {
        vm.init()

        val mappedTab = when (initialTab) {
            FollowTab.Followers -> FriendTab.Follower
            FollowTab.Following -> FriendTab.Following
        }

        vm.setTab(mappedTab)
    }

    if (ui.sessionExpired) {
        navController.navigate("error/404"){
            popUpTo(
                navController.currentBackStackEntry?.destination?.id ?: return@navigate
            ) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.bg1)
            .navigationBarsPadding()
    ) {
        TopBar(
            navController = navController,
            title = "친구",
            useCloseIcon = false,
            height = 48.dp
        )

        Spacer(Modifier.height(20.dp))
        FriendTabBar(
            selectedTab = ui.selectedTab,
            followerCount = ui.followerCount,
            followingCount = ui.followingCount,
            onTabSelected = vm::setTab
        )

        if (ui.loading && ui.followerItems.isEmpty() && ui.followingItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFBA00FF))
            }
        } else {
            when (ui.selectedTab) {
                FriendTab.Follower -> {
                    FollowerListContent(
                        items = ui.followerItems,
                        hasNext = ui.followerHasNext,
                        loadingMore = ui.loading,
                        onLoadNext = { vm.loadFollowerNext() },
                        onUnfollow = { vm.unfollowFromFollower(it) },
                        onFollow = { vm.followFromFollower(it) }
                    )
                }

                FriendTab.Following -> {
                    FollowingListContent(
                        items = ui.followingItems,
                        hasNext = ui.followingHasNext,
                        loadingMore = ui.loading,
                        onLoadNext = { vm.loadFollowingNext() },
                        onUnfollow = { vm.unfollowFromFollowing(it) }
                    )
                }
            }
        }

        if (ui.error != null) {
            Text(
                text = ui.error ?: "",
                color = Color.Red,
                fontFamily = pretendard,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun FriendTabBar(
    selectedTab: FriendTab,
    followerCount: Int,
    followingCount: Int,
    onTabSelected: (FriendTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(41.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FriendTabItem(
                text = "${followerCount} 팔로우",
                selected = selectedTab == FriendTab.Follower,
                onClick = { onTabSelected(FriendTab.Follower) },
                modifier = Modifier.weight(1f)
            )

            FriendTabItem(
                text = "${followingCount} 팔로잉",
                selected = selectedTab == FriendTab.Following,
                onClick = { onTabSelected(FriendTab.Following) },
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(AppColor.divider1)
        )

        Box(
            modifier = Modifier
                .align(
                    if (selectedTab == FriendTab.Follower) {
                        Alignment.BottomStart
                    } else {
                        Alignment.BottomEnd
                    }
                )
                .padding(horizontal = 16.dp)
                .fillMaxWidth(0.5f)
                .height(2.dp)
                .background(AppColor.Main2)
        )
    }
}

@Composable
private fun FriendTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = AppTypography.Label1,
            color = if (selected) AppColor.Main2 else AppColor.text4
        )
    }
}

@Composable
private fun FollowerListContent(
    items: List<FriendFollowerItem>,
    hasNext: Boolean,
    loadingMore: Boolean,
    onLoadNext: () -> Unit,
    onUnfollow: (Long) -> Unit,
    onFollow: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 30.dp)
        ) {
            if (items.isNotEmpty()) {
                items(items) { user ->
                    FollowerRow(user = user, onUnfollow = { onUnfollow(user.id) }, onFollow = { onFollow(user.id) })
                }
            }
        }

        if (hasNext || loadingMore) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (loadingMore) {
                    CircularProgressIndicator(
                        color = Color(0xFFBA00FF),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "더 보기",
                        fontSize = 14.sp,
                        fontFamily = pretendard,
                        color = Color(0xFFBA00FF),
                        modifier = Modifier.clickable { onLoadNext() }
                    )
                }
            }
        }
    }
}

@Composable
private fun FollowingListContent(
    items: List<FriendUFollowingItem>,
    hasNext: Boolean,
    loadingMore: Boolean,
    onLoadNext: () -> Unit,
    onUnfollow: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 30.dp)
        ) {
            if (items.isNotEmpty()) {
                items(items) { user ->
                    FollowingRow(user = user, onUnfollow = { onUnfollow(user.id) })
                }
            }
        }

        if (hasNext || loadingMore) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (loadingMore) {
                    CircularProgressIndicator(
                        color = Color(0xFFBA00FF),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "더 보기",
                        fontSize = 14.sp,
                        fontFamily = pretendard,
                        color = Color(0xFFBA00FF),
                        modifier = Modifier.clickable { onLoadNext() }
                    )
                }
            }
        }
    }
}

@Composable
private fun FollowerRow(
    user: FriendFollowerItem,
    onUnfollow: () -> Unit,
    onFollow: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ProfilePalette.idToColor(user.profileImgNumber)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile",
                tint = Color.White,
                modifier = Modifier.size(20.dp, 25.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.nickname,
                style = AppTypography.Label1,
                color = AppColor.text1
            )
            Text(
                text = "@" + user.handle,
                style = AppTypography.Label2,
                color = AppColor.text3
            )
        }
        if(user.isFollowing){
            InlineButton(
                text = "팔로우 취소",
                onClick = onUnfollow,
                style = AppTypography.Label2,
                color = AppColor.text3,
                state = InlineButtonState.Stroke,
                modifier = Modifier.size(92.dp, 32.dp)
            )
        } else {
            InlineButton(
                text = "팔로우",
                onClick = onFollow,
                style = AppTypography.Label2,
                modifier = Modifier.size(66.dp, 32.dp),
                color = AppColor.CTA_text
            )
        }

    }
}

@Composable
private fun FollowingRow(
    user: FriendUFollowingItem,
    onUnfollow: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(ProfilePalette.idToColor(user.profileImgNumber)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile",
                tint = Color.White,
                modifier = Modifier.size(20.dp, 25.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.nickname,
                style = AppTypography.Label1,
                color = AppColor.text1
            )
            Text(
                text = "@" + user.handle,
                style = AppTypography.Label2,
                color = AppColor.text3
            )
        }

        InlineButton(
            text = "팔로우 취소",
            onClick = onUnfollow,
            style = AppTypography.Label2,
            color = AppColor.text3,
            state = InlineButtonState.Stroke,
            modifier = Modifier.size(92.dp, 32.dp)
        )
    }
}
