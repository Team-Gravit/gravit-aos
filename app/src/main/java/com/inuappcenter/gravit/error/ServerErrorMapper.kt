package com.inuappcenter.gravit.error

import com.google.gson.JsonParser

fun mapServerError(raw: String?): String {
    if (raw.isNullOrBlank()) return "오류가 발생했습니다."

    return try {
        val json = JsonParser.parseString(raw).asJsonObject
        val errorCode = json.get("error")?.asString

        if (errorCode == "GLOBAL_5001") {
            return "오류가 발생했습니다."
        }

        val message = json.get("message")

        val parsedMessage = when {
            message == null || message.isJsonNull -> null

            message.isJsonArray -> {
                message.asJsonArray.joinToString("\n") {
                    it.asString
                }
            }

            message.isJsonPrimitive -> {
                message.asString
            }

            else -> null
        }

        parsedMessage
            ?.takeIf { it.isNotBlank() }
            ?: "오류가 발생했습니다."

    } catch (e: Exception) {
        "오류가 발생했습니다."
    }
}