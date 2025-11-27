package com.example.gravit.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.ui.theme.DesignSpec
import com.example.gravit.ui.theme.LocalDesignSpec
import com.example.gravit.R
import com.example.gravit.ui.theme.Responsive
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.error.isDeletionPending
import com.example.gravit.ui.theme.pretendard
import com.example.gravit.ui.theme.ProfilePalette



@Composable
fun ProfileSetting(navController: NavController) {
    val context = LocalContext.current

    val vm: OnboardingViewModel = viewModel(
        factory = OnboardingVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    var nickname by remember { mutableStateOf("") }
    var profileNo by remember { mutableIntStateOf(ProfilePalette.DEFAULT_ID) }
    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(ui) {
        when (ui) {
            OnboardingViewModel.UiState.Success -> {
                navigated = true
                navController.navigate("profile finish") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            OnboardingViewModel.UiState.SessionExpired -> {
                navigated = true
                navController.navigate("error/401") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            OnboardingViewModel.UiState.NotFound -> {
                if (isDeletionPending(context)) return@LaunchedEffect
                navigated = true
                navController.navigate("error/404") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            OnboardingViewModel.UiState.Failed -> {
                navController.navigate("login choice") {
                    popUpTo(0); launchSingleTop = true; restoreState = false
                }
            }
            else -> Unit
        }
    }

    CompositionLocalProvider(
        LocalDesignSpec provides DesignSpec(375f, 812f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(Modifier.height(Responsive.h(8f)))
                Box(modifier = Modifier.fillMaxWidth()) {
                    ImageButton(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "back arrow",
                        modifier = Modifier
                            .size(Responsive.w(48f))
                            .padding(start = Responsive.w(14f)),
                        onClick = {
                            navController.navigate("login choice") {
                                popUpTo(0); launchSingleTop = true; restoreState = false
                            }
                        }
                    )
                    Text(
                        text = "로그인",
                        modifier = Modifier.align(Alignment.Center),
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = Responsive.spH(20f)
                        )
                    )
                }

                Spacer(Modifier.height(Responsive.h(30f)))

                ProfileSwitcher(
                    selectedId = profileNo,
                    onProfileSelected = { newId -> profileNo = newId }
                )

                Spacer(Modifier.height(Responsive.h(30f)))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Responsive.w(25f))
                ) {
                    Text(
                        text = "닉네임 설정",
                        style = TextStyle(
                            fontFamily = pretendard,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = Responsive.spH(18f)
                        )
                    )
                    Spacer(Modifier.height(Responsive.h(12f)))
                    NameInputFiled(
                        text = nickname,
                        onTextChange = { nickname = it }
                    )
                }
            }

            CustomButton(
                text = "다음",
                onClick = {
                    if (isValidNickname(nickname) && ui !is OnboardingViewModel.UiState.Loading) {
                        vm.submit(nickname, profileNo)
                    }
                },
                enabled = isValidNickname(nickname) && ui !is OnboardingViewModel.UiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(
                        start = Responsive.w(25f),
                        end = Responsive.w(25f),
                        bottom = Responsive.h(14f)
                    )
                    .height(Responsive.h(60f))
            )

            if (ui is OnboardingViewModel.UiState.Loading) {
                Box(
                    Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
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
        modifier = modifier.clickable { onClick() },
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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ImageButton(
            painter = painterResource(id = R.drawable.arrow_left),
            contentDescription = "Previous profile",
            modifier = Modifier.size(Responsive.w(48f)),
            onClick = {
                currentIndex = (currentIndex - 1 + ProfilePalette.size) % ProfilePalette.size
                onProfileSelected(ProfilePalette.indexToId(currentIndex))
            },
            color = Color(0xFFC6C6C6)
        )
        Spacer(Modifier.width(Responsive.w(40f)))
        Box(
            modifier = Modifier
                .size(Responsive.h(178f))
                .clip(CircleShape)
                .background(ProfilePalette.colors[currentIndex]),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile_logo),
                contentDescription = "profile logo",
                modifier = Modifier.size(Responsive.w(72f), Responsive.h(90.89f))
            )
        }
        Spacer(Modifier.width(Responsive.w(40f)))
        ImageButton(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "Next profile",
            modifier = Modifier.size(Responsive.w(48f)),
            onClick = {
                currentIndex = (currentIndex + 1) % ProfilePalette.size
                onProfileSelected(ProfilePalette.indexToId(currentIndex))
            },
            color = Color(0xFFC6C6C6)
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

    Column {
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
                        text = "닉네임",
                        color = Color(0xFF868686),
                        fontFamily = pretendard,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            },
            textStyle = TextStyle(
                color = if (isError) Color.Red else Color.Black,
                fontFamily = pretendard,
                fontSize = Responsive.spW(18f),
                textAlign = TextAlign.Start
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(Responsive.h(10f)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedIndicatorColor = if (isEmpty) Color(0xFFC3C3C3) else if (isError) Color.Red else Color.Blue,
                unfocusedIndicatorColor = if (isEmpty) Color(0xFFC3C3C3) else if (isError) Color.Red else Color.Blue,
                errorIndicatorColor = Color.Red,
                cursorColor = if (isEmpty) Color(0xFFC3C3C3) else if (isError) Color.Red else Color.Blue
            )
        )
        Spacer(Modifier.height(Responsive.h(8f)))

        if (isEmpty) {
            Text(
                text = "*글자수 2~8자\n*공백, 특수문자 제외",
                color = Color(0xFF868686),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = Responsive.spH(12f)
                )
            )
        } else {
            Text(
                text = if (isError) "사용할 수 없는 닉네임이에요." else "사용 가능한 닉네임이에요.",
                color = Color(0xFF868686),
                style = TextStyle(
                    fontFamily = pretendard,
                    fontWeight = FontWeight.Normal,
                    fontSize = Responsive.spH(12f)
                )
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
