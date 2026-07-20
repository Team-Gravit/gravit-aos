package com.inuappcenter.gravit.fcm

import android.content.Context
import java.util.UUID
import androidx.core.content.edit

object DeviceIdManager {
    private const val PREF_NAME = "device_pref"
    private const val KEY_DEVICE_ID = "device_id"

    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return prefs.getString(KEY_DEVICE_ID, null) ?: run {
            val deviceId = UUID.randomUUID().toString()
            prefs.edit { putString(KEY_DEVICE_ID, deviceId) }
            deviceId
        }
    }

    fun retryGetDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val deviceId = UUID.randomUUID().toString()
        prefs.edit { putString(KEY_DEVICE_ID, deviceId) }

        return deviceId
    }

}

