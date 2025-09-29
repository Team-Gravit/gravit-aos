package com.example.gravit.main.Chapter.Lesson

import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gravit.ui.theme.pretendard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {

    data class State(
        val elapsedMillis: Long = 0L,
        val running: Boolean = false
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var ticker: Job? = null
    private var baseRealtime: Long = 0L   // start 기준점
    private var accumulated: Long = 0L    // 일시정지까지의 누적

    fun start() {
        if (_state.value.running) return
        baseRealtime = SystemClock.elapsedRealtime()
        _state.value = _state.value.copy(running = true)
        startTicker()
    }

    fun pause() {
        if (!_state.value.running) return
        accumulated += SystemClock.elapsedRealtime() - baseRealtime
        stopTicker()
        _state.value = _state.value.copy(running = false, elapsedMillis = accumulated)
    }

    fun reset() {
        stopTicker()
        accumulated = 0L
        baseRealtime = 0L
        _state.value = State()
    }

    private fun startTicker() {
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (true) {
                val now = SystemClock.elapsedRealtime()
                val elapsed = accumulated + (now - baseRealtime)
                _state.value = _state.value.copy(elapsedMillis = elapsed, running = true)
                delay(50)
            }
        }
    }

    private fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }
}

fun Format(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = (totalSeconds / 60)
    val seconds = (totalSeconds % 60)
    return "%02d:%02d".format(minutes, seconds)
}

fun FormatSeconds(seconds: Int): String {
    val minutes = seconds / 60
    val sec = seconds % 60
    return "%02d:%02d".format(minutes, sec)
}

@Composable
fun Stopwatch(
    vm: StopwatchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    autoStart: Boolean = true,
    modifier: Modifier = Modifier
) {
    val s by vm.state.collectAsState()

    LaunchedEffect(autoStart) {
        if (autoStart) vm.start()
    }
    DisposableEffect(Unit) {
        onDispose { vm.pause() }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = Format(s.elapsedMillis),
            fontSize = 12.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF494949),
            style = TextStyle(fontFeatureSettings = "tnum")
        )
    }
}