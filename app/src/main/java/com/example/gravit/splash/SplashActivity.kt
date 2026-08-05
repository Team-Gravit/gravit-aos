package com.inuappcenter.gravit.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.inuappcenter.gravit.BuildConfig
import com.inuappcenter.gravit.api.AuthPrefs
import com.inuappcenter.gravit.R
import kotlinx.coroutines.delay
import androidx.core.net.toUri
import com.inuappcenter.gravit.api.RetrofitInstance.api
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
            delay(300)

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
    AlertDialog(
        onDismissRequest = {
        },
        confirmButton = {
            TextButton(onClick = onUpdateClick) {
                Text("업데이트")
            }
        },
        title = {
            Text("업데이트가 필요합니다")
        },
        text = {
            Text("안정적인 서비스 이용을 위해 최신 버전으로 업데이트해 주세요.")
        },
        dismissButton = {
            TextButton(
                onClick = onCloseClick
            ) {
                Text("닫기")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
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
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text("네트워크 문제")
        },
        text = {
            Text("인터넷 연결이 원활하지 않습니다.")
        },
        confirmButton = {
            TextButton(
                onClick = onRetryClick
            ) {
                Text("다시 시도")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCloseClick
            ) {
                Text("닫기")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}