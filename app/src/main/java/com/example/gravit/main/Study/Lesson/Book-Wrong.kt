package com.inuappcenter.gravit.main.Study.Lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Study.Problem.LessonVMFactory
import com.inuappcenter.gravit.main.Study.Problem.LessonViewModel
import com.inuappcenter.gravit.main.Study.Problem.LoadingScreen
import com.inuappcenter.gravit.main.Study.Problem.ProblemUI
import com.inuappcenter.gravit.main.Study.Problem.StopwatchViewModel
import com.inuappcenter.gravit.ui.theme.pretendard
import kotlinx.coroutines.delay

@Composable
fun BookWrongScreen(
    navController: NavController,
    unitId: Int,
    onSessionExpired: () -> Unit,
    type: String
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

    val context = LocalContext.current
    val vm: LessonViewModel = viewModel(
        factory = LessonVMFactory(RetrofitInstance.api, context)
    )

    LaunchedEffect(unitId, type) {
        vm.load(unitId = unitId, type = type)
        vm.resetProblemSubmit()
    }

    val ui by vm.state.collectAsState()
    val problemSubmit by vm.problemSubmit.collectAsState()

    var minLoadingPassed by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(2000)
        minLoadingPassed = true
    }

    var navigated by remember { mutableStateOf(false) }
    val navTarget: String? = when {
        ui is LessonViewModel.UiState.SessionExpired ||
                problemSubmit is LessonViewModel.ProblemSubmitState.SessionExpired -> "401"

        ui is LessonViewModel.UiState.NotFound ||
                problemSubmit is LessonViewModel.ProblemSubmitState.NotFound -> "404"

        ui is LessonViewModel.UiState.Failed ||
                problemSubmit is LessonViewModel.ProblemSubmitState.Failed -> "FAILED"

        else -> null
    }

    LaunchedEffect(navTarget) {
        if (navTarget == null || navigated) return@LaunchedEffect
        navigated = true
        when (navTarget) {
            "401" -> {
                navController.navigate("error/401") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            "404" -> {
                navController.navigate("error/404") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.id ?: return@navigate
                    ) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
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
                LoadingScreen()
            }

            ui is LessonViewModel.UiState.Success -> {
                val s = (ui as LessonViewModel.UiState.Success).data
                val problems = s.problems

                val problemSlots = remember(problems) { problems }
                val total = problemSlots.size

                fun submitSingleProblem(problemId: Int, isCorrect: Boolean) {
                    vm.submitProblemResults(
                        ProblemSubmissionRequests(problemId, isCorrect)
                    ) { ok ->

                    }
                }

                ProblemUI(
                    navController = navController,
                    unitTitle = s.unitSummaryResponse.title,
                    problems = problemSlots,
                    total = total,
                    swVm = swVm,
                    onRecordResult = { id, correct ->
                        submitSingleProblem(id, correct)
                    },
                    bookmarkMap = bookmarkMap,
                    onBookmarkToggle = { problemId -> vm.toggleBookmark(problemId) },
                    onFinishLesson = { navController.popBackStack() },
                    type = type,
                    onRemoveWrongNote = { problemId ->
                        if (type == "wrong-answered-notes") {
                            vm.removeWrongAnswered(problemId) { ok ->
                            }
                        }
                    },
                    unitId = unitId
                )
            }

            ui is LessonViewModel.UiState.Failed -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF2F2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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