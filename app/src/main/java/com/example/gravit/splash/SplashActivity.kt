package com.inuappcenter.gravit.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.inuappcenter.gravit.BuildConfig
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.R
import kotlinx.coroutines.delay
import androidx.core.net.toUri
import com.example.gravit.share.StatusBarStyle
import com.example.gravit.ui.theme.AppColor
import com.example.gravit.ui.theme.AppTypography
import com.inuappcenter.gravit.api.RetrofitInstance.api
import com.inuappcenter.gravit.main.Study.Problem.ReportButton
import com.kakao.sdk.common.util.Utility
import kotlinx.coroutines.launch


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun SplashScreen(
    navController: NavController,
) {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp.dp
    val heightDp = configuration.screenHeightDp.dp
    val density = LocalDensity.current

    val widthPx = with(density) { widthDp.toPx() }
    val heightPx = with(density) { heightDp.toPx() }

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8100B3),
            Color(0xFFDD00FF)
        ),
        start = Offset(0f, heightPx),
        end = Offset(widthPx, 0f)
    )

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showForceUpdateDialog by remember { mutableStateOf(false) }
    var showNetworkErrorDialog by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }

    val activity = context as? Activity

    suspend fun startSplash() {
        Log.d("KAKAO", Utility.getKeyHash(context))
        if (isChecking) return

        isChecking = true
        showNetworkErrorDialog = false

        try {
            delay(1000)

            val versionResponse = api.getVersion()

            if (isVersionLower(currentVersion = BuildConfig.VERSION_NAME, serverVersion = versionResponse.version)) {
                showForceUpdateDialog = true
                return
            }

            val session = AuthPrefs.load(context)

            when {
                session == null -> {
                    AuthPrefs.clear(context)

                    navController.navigate("login choice") {
                        popUpTo(0)
                        launchSingleTop = true
                        restoreState = false
                    }
                }

                session.isOnboarded -> {
                    navController.navigate("main") {
                        popUpTo(0)
                        launchSingleTop = true
                        restoreState = false
                    }
                }

                else -> {
                    AuthPrefs.clear(context)

                    navController.navigate("login choice") {
                        popUpTo(0)
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(
                "VERSION_CHECK",
                "서버 연결 실패",
                e
            )
            showNetworkErrorDialog = true
        } finally {
            isChecking = false
        }
    }

    LaunchedEffect(Unit) {
        startSplash()
    }
    StatusBarStyle(darkIcons = false)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.splash_gravit_logo
            ),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
    }

    if (showForceUpdateDialog) {
        ForceUpdateDialog(
            onUpdateClick = {
                openPlayStore(context)
            },
            onCloseClick = {
                activity?.finishAffinity()
            }
        )
    }

    if (showNetworkErrorDialog) {
        NetworkErrorDialog(
            onRetryClick = {
                coroutineScope.launch {
                    startSplash()
                }
            },
            onCloseClick = {
                activity?.finishAffinity()
            }
        )
    }
}
fun isVersionLower(
    currentVersion: String,
    serverVersion: String
): Boolean {
    val currentParts = currentVersion.split(".").map { it.toIntOrNull() ?: 0 }
    val serverParts = serverVersion.split(".").map { it.toIntOrNull() ?: 0 }

    val maxSize = maxOf(currentParts.size, serverParts.size)

    for (index in 0 until maxSize) {
        val current = currentParts.getOrElse(index) { 0 }
        val server = serverParts.getOrElse(index) { 0 }

        if (current < server) return true
        if (current > server) return false
    }

    return false
}

@Composable
fun ForceUpdateDialog(
    onUpdateClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        BackHandler {
            onCloseClick()
        }
        Surface(
            modifier = Modifier
                .width(328.dp)
                .padding(bottom = 16.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .width(328.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "업데이트가 필요합니다",
                    style = AppTypography.Heading1,
                    textAlign = TextAlign.Center,
                    color = AppColor.text1
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "안정적인 서비스 이용을 위해\n최신 버전으로 업데이트해 주세요.",
                    style = AppTypography.Label1,
                    color = AppColor.text4,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                ReportButton(
                    onClick1 = onCloseClick,
                    text1 = "닫기",
                    onClick2 = onUpdateClick,
                    text2 = "업데이트",
                    modifier = Modifier,
                    modifier1 = Modifier
                        .weight(1f)
                        .height(50.dp),
                    modifier2 = Modifier
                        .weight(1f)
                        .height(50.dp),

                    )
            }
        }
    }
}
fun openPlayStore(context: Context) {
    val packageName = context.packageName

    val playStoreIntent = Intent(
        Intent.ACTION_VIEW,
        "market://details?id=$packageName".toUri()
    ).apply {
        setPackage("com.android.vending")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val webIntent = Intent(
        Intent.ACTION_VIEW,
        "https://play.google.com/store/apps/details?id=$packageName".toUri()
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(playStoreIntent)
    } catch (e: ActivityNotFoundException) {
        context.startActivity(webIntent)
    }
}

@Composable
fun NetworkErrorDialog(
    onRetryClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        BackHandler {
            onCloseClick()
        }
        Surface(
            modifier = Modifier
                .width(328.dp)
                .padding(bottom = 16.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .width(328.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "네트워크 문제",
                    style = AppTypography.Heading1,
                    textAlign = TextAlign.Center,
                    color = AppColor.text1
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "연결이 원활하지 않습니다.",
                    style = AppTypography.Label1,
                    color = AppColor.text4,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))
                ReportButton(
                    onClick1 = onCloseClick,
                    text1 = "닫기",
                    onClick2 = onRetryClick,
                    text2 = "다시 시도",
                    modifier = Modifier,
                    modifier1 = Modifier
                        .weight(1f)
                        .height(50.dp),
                    modifier2 = Modifier
                        .weight(1f)
                        .height(50.dp),

                    )
            }
        }
    }
}