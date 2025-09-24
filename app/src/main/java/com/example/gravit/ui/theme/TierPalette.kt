package com.example.gravit.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.gravit.R
import com.example.gravit.ui.theme.ProfilePalette.colors

object TierPalette {
    val tiers: List<Int> = listOf(
        R.drawable.bronze1,
        R.drawable.bronze2,
        R.drawable.bronze3,
        R.drawable.silver1,
        R.drawable.silver2,
        R.drawable.silver3,
        R.drawable.gold1,
        R.drawable.gold2,
        R.drawable.gold3,
        R.drawable.platinum1,
        R.drawable.platinum2,
        R.drawable.platinum3,
        R.drawable.diamond1,
        R.drawable.diamond2,
        R.drawable.diamond3,
    )
    const val DEFAULT_ID = 1
    fun resIdFor(tierId: Int?): Int {
        val idx = ((tierId ?: DEFAULT_ID) - 1).coerceIn(0, tiers.lastIndex)
        return tiers[idx]
    }
    @Composable
    fun painterFor(tierId: Int?): Painter = painterResource(id = resIdFor(tierId))

}
