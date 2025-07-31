package com.example.gravit.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp

val LocalScreenWidth = compositionLocalOf<Dp> { error("No ScreenWidth provided") }
val LocalScreenHeight = compositionLocalOf<Dp> { error("No ScreenHeight provided") }
