package com.example.gravit.main.User.Notice

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun formatIsoToYmd(iso: String): String {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val odt = OffsetDateTime.parse(iso)
            odt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        } else {
            val inFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = inFmt.parse(iso)
            val outFmt = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            outFmt.format(requireNotNull(date))
        }
    } catch (_: Exception) {
        if (iso.length >= 10) iso.substring(0, 10).replace("-", ".") else iso
    }
}