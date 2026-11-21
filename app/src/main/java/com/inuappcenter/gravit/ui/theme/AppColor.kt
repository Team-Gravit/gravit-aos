package com.inuappcenter.gravit.ui.theme

import androidx.compose.ui.graphics.Color

object AppColor {

    val black = PrimitiveColor.Gray1100
    val white = PrimitiveColor.Gray0


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
    val text_on_color_1 = PrimitiveColor.Gray0
    val text_on_color_2 = PrimitiveColor.Gray300
    val text_on_color_3 = PrimitiveColor.Gray500


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

    val CTA_pressed = PrimitiveColor.Purple950

    val CTA_secondary_bg = PrimitiveColor.Purple50
    val CTA_secondary_bg_hover = PrimitiveColor.Purple100
    val CTA_secondary_bg_pressed = PrimitiveColor.Purple200
    val CTA_secondary_label = Main1

    val CTA_tertiary_border = Main1
    val CTA_tertiary_bg_hover = PrimitiveColor.Purple50
    val CTA_tertiary_bg_pressed = PrimitiveColor.Purple100
    val CTA_tertiary_label = Main1

    val CTA_label_disabled = PrimitiveColor.Gray500


    // Icon
    val icon_default = PrimitiveColor.Gray600
    val icon_brand = Main2
    val icon_disabled = PrimitiveColor.Gray500
    val icon_on_color = PrimitiveColor.Gray0

    // Status
    val status_error = Color(0xFFEB3D32)
    val status_success = Color(0xFF00C30D)
    val status_warning = Color(0xFFF9A825)
    val status_info = PrimitiveColor.Gray400
    val status_accent = Color(0xFF1FABFF)

    // Status Subtle
    val status_error_subtle = status_error.copy(alpha = 0.1f)
    val status_success_subtle = status_success.copy(alpha = 0.1f)
    val status_warning_subtle = status_warning.copy(alpha = 0.1f)
    val status_accent_subtle = status_accent.copy(alpha = 0.1f)


    //Chart
    val chart_accuracy = PrimitiveColor.Gray200
    val chart_delivery = PrimitiveColor.Gray500
}