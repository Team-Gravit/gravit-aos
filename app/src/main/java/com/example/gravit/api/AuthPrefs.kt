package com.example.gravit.api

import android.content.Context
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object AuthPrefs {
    private const val PREF = "auth"
    private const val K_ACCESS_TOKEN = "accessToken"
    private const val K_ONBOARDED = "isOnboarded"
    private const val K_EXPIRES_AT = "expiresAt"

    data class Session(
        val accessToken: String,
        val isOnboarded: Boolean,
        val expiresAtMillis: Long
    )

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREF,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun save(context: Context, accessToken: String, isOnboarded: Boolean) {
        val exp = decodeExpMillis(accessToken)
        if (exp == null) {
            Log.e("AuthPrefs", "exp가 없어 세션을 저장하지 않습니다.")
            clear(context)
            return
        }

        val now = System.currentTimeMillis()
        val leewayMs = 30_000L //여유 시간 30초
        if (exp <= now + leewayMs) { //만료됐거나 30초 안에 만료될 예정이면 저장하지 않음
            Log.w("AuthPrefs", "토큰이 이미 만료/임박(exp=$exp, now=$now). 저장하지 않습니다.")
            clear(context)
            return
        }

        prefs(context).edit(commit = true) { //동기 커밋으로 레이스 방지
            putString(K_ACCESS_TOKEN, accessToken)
            putBoolean(K_ONBOARDED, isOnboarded)
            putLong(K_EXPIRES_AT, exp)
        }

        Log.d("AuthPrefs", "Saved: accessToken=${maskToken(accessToken)}, isOnboarded=$isOnboarded, exp=$exp")
    }

    fun load(context: Context): Session? {
        val sp = prefs(context)
        val token = sp.getString(K_ACCESS_TOKEN, null) ?: return null
        val exp = sp.getLong(K_EXPIRES_AT, -1L).takeIf { it > 0 } ?: return null
        val ob = sp.getBoolean(K_ONBOARDED, false)
        return Session(token, ob, exp)
    }

    fun clear(context: Context) {
        prefs(context).edit(commit = true) { clear() }
    }


    fun isExpired(
        session: Session?,
        now: Long = System.currentTimeMillis(),
        leewaySec: Long = 30
    ): Boolean {
        return session == null || now >= (session.expiresAtMillis - leewaySec * 1000)
    }



    fun setOnboarded(context: Context, value: Boolean) {
        prefs(context).edit(commit = true) { putBoolean(K_ONBOARDED, value) }
    }

    private fun decodeExpMillis(jwt: String): Long? = runCatching {
        // "header.payload.signature" (JWS) 형태만 허용
        val parts = jwt.split('.')
        if (parts.size != 3) return@runCatching null

        // Base64URL 디코딩 후 UTF-8로 JSON 파싱
        val payloadBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val obj = JSONObject(String(payloadBytes, Charsets.UTF_8))

        // exp가 숫자/문자열 모두 올 수 있으니 안전하게 파싱
        val expSec: Long = when (val v = obj.opt("exp")) {
            is Number -> v.toLong()
            is String -> v.toLongOrNull() ?: return@runCatching null
            else -> return@runCatching null
        }

        if (expSec <= 0) return@runCatching null

        // 초 → 밀리초 변환하여 반환
        expSec * 1000L
    }.getOrNull()


}

// 이미 있는 함수 재사용
fun maskToken(t: String?): String =
    if (t.isNullOrBlank()) "null" else "${t.take(6)}...${t.takeLast(6)} (len=${t.length})"
