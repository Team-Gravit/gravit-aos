package com.example.gravit.api

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object AuthPrefs {
    private const val PREF = "auth"
    private const val K_ACCESS_TOKEN = "accessToken"
    private const val K_REFRESHTOKEN = "refreshToken"
    private const val K_ONBOARDED = "isOnboarded"

    data class Session(
        val accessToken: String,
        val refreshToken: String,
        val isOnboarded: Boolean,
    )

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREF,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun save(context: Context, accessToken: String, refreshToken: String, isOnboarded: Boolean) {
        prefs(context).edit(commit = true) {
            putString(K_ACCESS_TOKEN, accessToken)
            putString(K_REFRESHTOKEN, refreshToken)
            putBoolean(K_ONBOARDED, isOnboarded)
        }
    }

    fun load(context: Context): Session? {
        val sp = prefs(context)
        val token = sp.getString(K_ACCESS_TOKEN, null) ?: return null
        val refreshToken = sp.getString(K_REFRESHTOKEN, null) ?: return null
        val ob = sp.getBoolean(K_ONBOARDED, false)
        return Session(token, refreshToken, ob)
    }

    fun clear(context: Context) {
        prefs(context).edit(commit = true) { clear() }
    }

    fun setOnboarded(context: Context, value: Boolean) {
        prefs(context).edit(commit = true) { putBoolean(K_ONBOARDED, value) }
    }
}