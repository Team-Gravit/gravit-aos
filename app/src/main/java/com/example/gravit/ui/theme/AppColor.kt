package com.example.gravit.ui.theme

import androidx.compose.ui.graphics.Color

object AppColor {

    // Background
    val bg0 = PrimitiveColor.Gray0
    val bg1 = PrimitiveColor.Gray100
    val bg2 = PrimitiveColor.Gray200
    val bg3 = PrimitiveColor.Gray300
    val bg4 = PrimitiveColor.Gray400

    // Text
    val text1 = PrimitiveColor.Gray1000
    val text2 = PrimitiveColor.Gray900
    val text3 = PrimitiveColor.Gray600
    val text4 = PrimitiveColor.Gray500

    // Text on color
    val text1w = PrimitiveColor.Gray0
    val text2w = PrimitiveColor.Gray300
    val text3w = PrimitiveColor.Gray500

    // Divider
    val divider1 = PrimitiveColor.Gray300
    val divider2 = PrimitiveColor.Gray400

    // Brand
    val Main1 = PrimitiveColor.Purple600
    val Main2 = PrimitiveColor.Purple700

    // CTA
    val CTA = Main2
    val CTA_hover = PrimitiveColor.Purple900
    val CTA_text = PrimitiveColor.Gray50
    val CTA_disabled = PrimitiveColor.Gray300
    val CTA_disabled_text = Main2

    val CTA_secondary = PrimitiveColor.Gray300
    val CTA_secondary_hover = PrimitiveColor.Gray500
    val CTA_secondary_text = text3

    // Icon
    val icon_default = PrimitiveColor.Gray600
    val icon_color = Main2
    val icon_disabled = PrimitiveColor.Gray500
    val icon_w = PrimitiveColor.Gray0

    // Status
    val errorColor = Color(0xFFEB3D32)
    val successColor = Color(0xFF00C30D)
    val warningColor = Color(0xFFF9A825)
    val infoColor = PrimitiveColor.Gray400
    val accentColor = Color(0xFF1FABFF)

    // Status Subtle
    val errorSubtle = Color(0x1AEB3D32)
    val successSubtle = Color(0x1A00C30D)
    val warningSubtle = Color(0x1AF9A825)
    val accentSubtle = Color(0x1A1FABFF)
}