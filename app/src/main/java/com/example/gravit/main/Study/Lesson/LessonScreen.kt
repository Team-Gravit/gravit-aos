package com.inuappcenter.gravit.main.Study.Lesson

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.inuappcenter.gravit.api.ProblemSubmissionRequests
import com.inuappcenter.gravit.api.Problems
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Study.Problem.LessonVMFactory
import com.inuappcenter.gravit.main.Study.Problem.LessonViewModel
import com.inuappcenter.gravit.main.Study.Problem.LoadingScreen
import com.inuappcenter.gravit.main.Study.Problem.ProblemUI
import com.inuappcenter.gravit.main.Study.Problem.StopwatchViewModel
import com.inuappcenter.gravit.ui.theme.pretendard
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    navController: NavController,
    lessonId: Int,
    onSessionExpired: () -> Unit
) {
    val swVm: StopwatchViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                swVm.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)

        onDispose {
            swVm.pause()
            lifecycleOwner.lifecycle.removeObserver(obs)
        }
    }
    val resultsMap = remember { mutableStateMapOf<Int, ProblemSubmissionRequests>() }
    var submitting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

    LaunchedEffect(lessonId) {
        resultsMap.clear()
        vm.load(lessonId = lessonId, type = "normal")
    }

    val ui by vm.state.collectAsState()

    var minLoadingPassed by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(2000)
        minLoadingPassed = true
    }

    LaunchedEffect(ui) {
        when (ui) {
            LessonViewModel.UiState.SessionExpired -> {
                navController.navigate("error/401") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            LessonViewModel.UiState.NotFound -> {
                navController.navigate("error/404") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            else -> Unit
        }
    }

    val bookmarkMap by vm.bookmark.collectAsState()
    val successState = ui as? LessonViewModel.UiState.Success
    val bookmarkReady = remember(bookmarkMap, successState) {
        successState?.let { bookmarkMap.size >= it.data.problems.size } ?: false
    }

    val isLoading = !minLoadingPassed ||
            ui is LessonViewModel.UiState.Loading ||
            (ui is LessonViewModel.UiState.Success && !bookmarkReady)

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                // LoadingScreen을 Box로 감싸서 크기 고정
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
                }
            }

            ui is LessonViewModel.UiState.Success -> {
                val s = (ui as LessonViewModel.UiState.Success).data
                val problems = s.problems
                val declaredTotal = s.totalProblems
                val rawTotal = if (declaredTotal > 0) declaredTotal else problems.size
                val problemSlots: List<Problems> = remember(problems, rawTotal) {
                    (0 until rawTotal)
                        .mapNotNull { idx -> problems.getOrNull(idx) }
                }
                val total = problemSlots.size

                LaunchedEffect(problems) {
                    vm.initBookmarks(problems)
                }

                fun recordResult(problemId: Int, isCorrect: Boolean) {
                    resultsMap[problemId] = ProblemSubmissionRequests(
                        problemId = problemId,
                        isCorrect = isCorrect
                    )
                }

                fun finishLesson() {
                    swVm.pause()
                    if (submitting) return
                    submitting = true

                    val problemSubmissionRequests = resultsMap.values.toList()
                    if (problemSubmissionRequests.isEmpty()) {
                        submitting = false
                        return
                    }
                    val learningTime = (swVm.state.value.elapsedMillis / 1000).toInt()
                    val correctCount = problemSubmissionRequests.count { it.isCorrect }
                    val accuracy: Int = if(total > 0) { ((correctCount.toDouble()/total) * 100).roundToInt() } else 0

                    val homeEntry = navController.getBackStackEntry("home")
                    homeEntry.savedStateHandle["problemList"] = ArrayList(problemSubmissionRequests)
                    navController.navigate(
                        "lesson/complete/$accuracy/$learningTime/$lessonId"
                    ) {
                        popUpTo("lesson/$lessonId") { inclusive = true }
                        launchSingleTop = true
                    }
                }

                ProblemUI(
                    navController = navController,
                    unitTitle = s.unitSummaryResponse.title,
                    problems = problemSlots,
                    total = total,
                    swVm = swVm,
                    bookmarkMap = bookmarkMap,
                    onBookmarkToggle = { problemId -> vm.toggleBookmark(problemId) },
                    onRecordResult = ::recordResult,
                    onFinishLesson = ::finishLesson,
                    unitId = s.unitSummaryResponse.unitId
                )
            }

            ui is LessonViewModel.UiState.Failed -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF2F2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                        Text(
                            text = "오류가 발생했습니다.\n다시 시도해 주세요.",
                            fontFamily = pretendard,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            border = BorderStroke(1.dp, Color.Black),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.Black,
                                containerColor = Color(0xFFF2F2F2)
                            ),
                            onClick = { navController.popBackStack() },
                        ) {
                            Text(
                                text = "이전으로",
                                fontFamily = pretendard
                            )
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}