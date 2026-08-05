package com.inuappcenter.gravit.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.example.gravit.ui.theme.BlockButton
import com.example.gravit.ui.theme.ButtonState
import com.example.gravit.ui.theme.PrimitiveColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Study.Problem.CustomSnackBar
import com.inuappcenter.gravit.ui.theme.ProfilePalette
import com.inuappcenter.gravit.ui.theme.pretendard
import com.inuappcenter.gravit.R
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.main.User.TopBar
import kotlinx.coroutines.delay


@Composable
fun ProfileSetting(navController: NavController) {
    val context = LocalContext.current

    val vm: OnboardingViewModel = viewModel(
        factory = OnboardingVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    var nickname by remember { mutableStateOf("") }
    var profileNo by remember { mutableIntStateOf(ProfilePalette.DEFAULT_ID) }
    var showSnackbar by remember { mutableStateOf(false) }


    LaunchedEffect(ui) {
        when (ui) {
            OnboardingViewModel.UiState.Success -> {
                navController.navigate("profile finish") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        vm.event.collect { event ->
            when (event) {
                OnboardingViewModel.Event.ShowFailedSnack -> {
                    showSnackbar = true
                    delay(2000)
                    showSnackbar = false
                }
            }
        }
    }
    val systemUiController = rememberSystemUiController()
    val isDarkMode = isSystemInDarkTheme()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !isDarkMode
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(navController, title = "로그인", useCloseIcon = false, isOnboarding= true)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                ProfileSwitcher(
                    selectedId = profileNo,
                    onProfileSelected = { newId -> profileNo = newId }
                )
                Text(
                    text = "닉네임 설정",
                    style = AppTypography.Heading2,
                    color = AppColor.text1
                )
                Spacer(Modifier.height(12.dp))
                NameInputFiled(
                    text = nickname,
                    onTextChange = { nickname = it }
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
                        text = "이전",
                        onClick = {
                            AuthPrefs.clear(context)
                            navController.popBackStack() },
                        state = ButtonState.Stroke,
                        style = AppTypography.Headline2,
                        modifier = Modifier.weight(1f)
                    )

                    BlockButton(
                        text = "다음",
                        onClick = {
                            vm.submit(nickname, profileNo)
                        },
                        enabled = isValidNickname(nickname) &&
                                ui !is OnboardingViewModel.UiState.Loading,
                        style = AppTypography.Headline2,
                        modifier = Modifier.weight(3f)
                    )
                }
            }
        }

        if (ui is OnboardingViewModel.UiState.Loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
        if (showSnackbar) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                CustomSnackBar(
                    text = "다시 시도해 주세요.",
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Composable
fun ImageButton(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    color: Color = Color.Black
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onClick()
        },
        colorFilter = ColorFilter.tint(color)
    )
}

@Composable
fun ProfileSwitcher(
    selectedId: Int = ProfilePalette.DEFAULT_ID,
    onProfileSelected: (Int) -> Unit = {}
) {
    var currentIndex by remember { mutableIntStateOf(ProfilePalette.idToIndex(selectedId)) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ImageButton(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = "Previous profile",
            modifier = Modifier
                .width(32.dp)
                .aspectRatio(1f / 2f),
            onClick = {
                currentIndex = (currentIndex - 1 + ProfilePalette.size) % ProfilePalette.size
                onProfileSelected(ProfilePalette.indexToId(currentIndex))
            },
            color = PrimitiveColor.Gray500
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(ProfilePalette.colors[currentIndex]),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile logo",
                modifier = Modifier.size(60.dp, 76.dp)
            )
        }
        ImageButton(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "Next profile",
            modifier = Modifier
                .width(32.dp)
                .aspectRatio(1f / 2f),
            onClick = {
                currentIndex = (currentIndex + 1) % ProfilePalette.size
                onProfileSelected(ProfilePalette.indexToId(currentIndex))
            },
            color = PrimitiveColor.Gray500
        )
    }
}

fun isValidNickname(nickname: String): Boolean {
    val regex = "^[가-힣a-zA-Z0-9]{2,8}$".toRegex()
    return regex.matches(nickname)
}

@Composable
fun NameInputFiled(
    text: String,
    onTextChange: (String) -> Unit,
) {
    val isValid = isValidNickname(text)
    val isEmpty = text.isEmpty()
    val isError = text.isNotEmpty() && !isValid

    Column{
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = true,
            isError = isError,
            placeholder = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "닉네임을 입력해주세요.",
                        style = AppTypography.Label1,
                        color = AppColor.text4
                    )
                }
            },
            textStyle = AppTypography.Label1.copy(color = AppColor.text1),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AppColor.bg0,
                unfocusedContainerColor = AppColor.bg0,
                errorContainerColor = AppColor.bg0,
                focusedIndicatorColor = if (isEmpty) AppColor.divider1 else if (isError) AppColor.errorColor else AppColor.successColor,
                unfocusedIndicatorColor = if (isEmpty) AppColor.divider1 else if (isError) AppColor.errorColor else AppColor.successColor,
                errorIndicatorColor = AppColor.errorColor,
                cursorColor = if (isEmpty) AppColor.divider1 else if (isError) AppColor.errorColor else AppColor.successColor
            )
        )
        Spacer(Modifier.height(12.dp))

        if (isEmpty) {
            Text(
                text = "*글자수 2~8자\n*공백, 특수문자 제외",
                color = AppColor.text4,
                style = AppTypography.Caption1
            )
        } else {
            Text(
                text = if (isError) "사용할 수 없는 닉네임이에요." else "사용 가능한 닉네임이에요.",
                color = if(isError) AppColor.errorColor else AppColor.successColor,
                style = AppTypography.Caption1
            )
        }
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val activeBackground = Color(0xFF8100B3)
    val inactiveBackground = activeBackground.copy(alpha = 0.5f)
    val activeTextColor = Color.White
    val inactiveTextColor = Color.White.copy(alpha = 0.5f)

    Button(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) activeBackground else inactiveBackground,
            contentColor = if (enabled) activeTextColor else inactiveTextColor,
            disabledContainerColor = inactiveBackground,
            disabledContentColor = inactiveTextColor
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
