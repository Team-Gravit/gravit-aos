package com.example.gravit.main.Home

import android.annotation.SuppressLint
import android.net.http.SslCertificate.restoreState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.api.ChapterPageResponse
import com.example.gravit.api.RetrofitInstance
import com.example.gravit.main.Chapter.ChapterVMFactory
import com.example.gravit.main.Chapter.ChapterViewModel

@SuppressLint("DefaultLocale")
@Composable
fun planetComplete(
    navController: NavController
): String {
    val context = LocalContext.current
    val vm: ChapterViewModel = viewModel(
        factory = ChapterVMFactory(RetrofitInstance.api, context)
    )
    val ui by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.load() }
    when (ui) {
        ChapterViewModel.UiState.SessionExpired -> {
            navController.navigate("login choice") {
                popUpTo(0)
                launchSingleTop = true
                restoreState = false
            }
        }
        else -> Unit
    }

    val chapters: List<ChapterPageResponse> =
        (ui as? ChapterViewModel.UiState.Success)?.data ?: emptyList()

    val totalCompleted = chapters.sumOf { it.completedUnits }
    val totalUnits = chapters.sumOf { it.totalUnits }

    val completeRate: Float = if (totalUnits > 0) {
        totalCompleted.toFloat() / totalUnits.toFloat()
    } else 0f

    val percentText = String.format("%.1f%%", completeRate * 100)


    return percentText
}