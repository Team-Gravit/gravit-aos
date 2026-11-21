package com.inuappcenter.gravit.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.inuappcenter.gravit.R

object TierPalette {
    val tiers: Map<Long, Int> = mapOf(
        1L to R.drawable.bronze3,
        2L to R.drawable.bronze2,
        3L to R.drawable.bronze1,
        4L to R.drawable.silver3,
        5L to R.drawable.silver2,
        6L to R.drawable.silver1,
        7L to R.drawable.gold3,
        8L to R.drawable.gold2,
        9L to R.drawable.gold1,
        10L to R.drawable.platinum3,
        11L to R.drawable.platinum2,
        12L to R.drawable.platinum1,
        13L to R.drawable.diamond3,
        14L to R.drawable.diamond2,
        15L to R.drawable.diamond1,
    )
    @DrawableRes
    fun resIdFor(tierId: Long?): Int =
        tiers[tierId] ?: R.drawable.bronze3
    @Composable
    fun painterFor(tierId: Long?): Painter = painterResource(id = resIdFor(tierId))

}
