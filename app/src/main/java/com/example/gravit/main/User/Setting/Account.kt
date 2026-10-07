package com.inuappcenter.gravit.main.User.Setting

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.login.NameInputFiled
import com.inuappcenter.gravit.login.ProfileSwitcher
import com.inuappcenter.gravit.login.isValidNickname
import com.inuappcenter.gravit.main.Study.Problem.CustomSnackBar
import com.inuappcenter.gravit.main.User.TopBar
import kotlinx.coroutines.delay

@Composable
fun Account(
    navController: NavController,
) {
    val context = LocalContext.current
    val vm: AccountVM = viewModel(
        factory = AccountVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.loadUserInfo() }

    val nicknameValid = isValidNickname(ui.nickname)
    val canSave = nicknameValid && !ui.isSaving
    var showSnackbar by remember { mutableStateOf(false) }
    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()

    LaunchedEffect(ui.errorMsg) {
        if (ui.errorMsg != null) {
            showSnackbar = true
            delay(2000)
            showSnackbar = false
        }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !isDarkMode
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(AppColor.bg0)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                navController = navController,
                title = "내 정보 수정"
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                key(ui.profileId) {
                    ProfileSwitcher(
                        selectedId = ui.profileId,
                        onProfileSelected = vm::onProfileChange
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "닉네임 설정",
                    style = AppTypography.Heading2,
                    color = AppColor.text1
                )
                Spacer(modifier = Modifier.height(12.dp))
                NameInputFiled(
                    text = ui.nickname,
                    onTextChange = vm::onNicknameChange
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BlockButton(
                        text = "돌아가기",
                        onClick = {
                            AuthPrefs.clear(context)
                            navController.popBackStack()
                        },
                        state = ButtonState.Stroke,
                        style = AppTypography.Headline2,
                        modifier = Modifier
                            .height(45.dp)
                            .weight(1f)
                    )
                    BlockButton(
                        text = "수정하기",
                        onClick = {
                            vm.save {
                                navController.popBackStack()
                            }
                        },
                        enabled = canSave,
                        style = AppTypography.Headline2,
                        modifier = Modifier
                            .height(45.dp)
                            .weight(1f)
                    )
                }
            }
        }
        if (showSnackbar) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                CustomSnackBar(
                    text = "다시 시도해 주세요.",
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }
    }
}