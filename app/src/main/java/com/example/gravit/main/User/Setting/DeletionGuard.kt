package com.example.gravit.main.User.Setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gravit.api.RetrofitInstance

@Composable
fun DeletionGuard(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val vm: DeleteAccountVM = viewModel(
        factory = DeleteAccountVMFactory(RetrofitInstance.api, context)
    )
    val state by vm.state.collectAsState()
    val latestState by rememberUpdatedState(state)
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        if (state is DeleteAccountVM.DeletionState.Pending) {
            vm.checkIfDeleted()
        }
    }
    DisposableEffect(owner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME &&
                latestState is DeleteAccountVM.DeletionState.Pending
            ) {
                vm.checkIfDeleted()
            }
        }
        owner.lifecycle.addObserver(obs)
        onDispose { owner.lifecycle.removeObserver(obs) }
    }
    // 결과에 따라 전역 네비
    LaunchedEffect(state) {
        when (state) {
            DeleteAccountVM.DeletionState.Confirmed -> {
                navController.navigate("user/deletion-complete") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }

            DeleteAccountVM.DeletionState.SessionExpired -> {
                navController.navigate("error/401") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            DeleteAccountVM.DeletionState.NotFound -> {
                navController.navigate("error/404") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            else -> Unit
        }
    }

    content()
}
