package com.inuappcenter.gravit.main.Study.Lesson

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gravit.share.LoadFailedDialog
import com.inuappcenter.gravit.api.ProblemSubmissionSaveRequests
import com.inuappcenter.gravit.api.RetrofitInstance
import com.inuappcenter.gravit.main.Study.Problem.LessonVMFactory
import com.inuappcenter.gravit.main.Study.Problem.LessonViewModel
import com.inuappcenter.gravit.main.Study.Problem.LoadingScreen
import com.inuappcenter.gravit.main.Study.Problem.ProblemUI
import com.inuappcenter.gravit.main.Study.Problem.StopwatchViewModel
import kotlinx.coroutines.delay

@Composable
fun BookWrongScreen(
    navController: NavController,
    unitId: Long,
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

        else -> null
    }

    LaunchedEffect(navTarget) {
        if (navTarget == null || navigated) return@LaunchedEffect
        navigated = true
        when (navTarget) {
            "401" -> {
                navController.navigate("error/401") {
                    launchSingleTop = true
                }
            }

            "404" -> {
                navController.navigate("error/404") {
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

                fun submitSingleProblem(
                    problemId: Long,
                    isCorrect: Boolean,
                    selectedOptionId: Long?,
                    submittedContent: String?,
                    onResult: (Boolean) -> Unit
                ) {
                    vm.submitProblemResults(
                        ProblemSubmissionSaveRequests(
                            problemId = problemId,
                            isCorrect = isCorrect,
                            selectedOptionId = selectedOptionId,
                            submittedContent = submittedContent
                        )
                    ) { success ->
                        onResult(success)
                    }
                }

                ProblemUI(
                    navController = navController,
                    unitTitle = s.unitSummaryResponse.title,
                    problems = problemSlots,
                    total = total,
                    swVm = swVm,
                    onRecordResult = ::submitSingleProblem,
                    bookmarkMap = bookmarkMap,
                    onBookmarkToggle = { problemId, onResult ->
                        vm.toggleBookmark(problemId, onResult)
                    },
                    onFinishLesson = { navController.popBackStack() },
                    type = type,
                    onRemoveWrongNote = { problemId, onResult ->
                        if (type == "wrong-answered-notes") {
                            vm.removeWrongAnswered(problemId) { ok ->
                                onResult(ok)
                            }
                        } else {
                            onResult(false)
                        }
                    },
                    unitId = unitId,
                    unitOderText = "Unit${s.unitSummaryResponse.displayOrder.toString().padStart(2, '0')}"
                )
            }
            ui is LessonViewModel.UiState.Failed -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoadingScreen()

                    LoadFailedDialog(
                        onRetryClick = {
                            vm.load(
                                unitId = unitId,
                                type = type
                            )
                        }
                    )
                }
            }

            else -> Unit
        }
    }
}