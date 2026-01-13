package com.inuappcenter.gravit.main.User.Setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.login.CustomButton
import com.inuappcenter.gravit.login.NameInputFiled
import com.inuappcenter.gravit.login.ProfileSwitcher
import com.inuappcenter.gravit.login.isValidNickname
import com.inuappcenter.gravit.main.User.TopBar
import com.inuappcenter.gravit.ui.theme.pretendard

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {

            TopBar(navController, title = "내 정보 수정")

            Spacer(Modifier.height(24.dp))
            key(ui.profileId) {
                ProfileSwitcher(
                    selectedId = ui.profileId,
                    onProfileSelected = vm::onProfileChange
                )
            }

            Spacer(Modifier.height(24.dp))
            Column(Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "닉네임 설정",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = pretendard,
                    color = Color.Black
                )
                Spacer(Modifier.height(12.dp))
                NameInputFiled(
                    text = ui.nickname,
                    onTextChange = vm::onNicknameChange
                )
                if (ui.errorMsg != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = ui.errorMsg ?: "",
                        color = Color(0xFFFF0000),
                        fontFamily = pretendard,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            CustomButton(
                text = if (ui.isSaving) "저장 중..." else "수정하기",
                onClick = {
                    vm.save {
                        navController.popBackStack()
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(56.dp)
            )
        }
    }
}