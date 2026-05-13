package com.inuappcenter.gravit.main.League

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.inuappcenter.gravit.ui.theme.gmarketsans
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("DefaultLocale")
@Composable
fun WeeklyCountdown() {
    var remainingMillis by remember { mutableStateOf(getMillisUntilNextMonday()) }

    LaunchedEffect(Unit) {
        while (true) {
            remainingMillis = getMillisUntilNextMonday()
            delay(1000L)
        }
    }

    val totalSeconds = remainingMillis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds / 60) % 60
    val seconds = totalSeconds % 60

    val timeText = String.format("%02d시간 %02d분 %02d초", hours, minutes, seconds)

    Text(
        text = timeText,
        fontFamily = gmarketsans,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF494949),

    )
}

fun getMillisUntilNextMonday(tz: TimeZone = TimeZone.getTimeZone("Asia/Seoul")): Long {
    val now = Calendar.getInstance(tz)
    val nowMs = now.timeInMillis

    val next = now.clone() as Calendar
    next.set(Calendar.HOUR_OF_DAY, 0)
    next.set(Calendar.MINUTE, 0)
    next.set(Calendar.SECOND, 0)
    next.set(Calendar.MILLISECOND, 0)

    val dayOfWeek = next.get(Calendar.DAY_OF_WEEK)
    val monday = Calendar.MONDAY
    var daysToAdd = (monday - dayOfWeek + 7) % 7
    if (daysToAdd == 0) daysToAdd = 7
    next.add(Calendar.DAY_OF_YEAR, daysToAdd)

    return next.timeInMillis - nowMs
}