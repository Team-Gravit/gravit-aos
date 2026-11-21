package com.inuappcenter.gravit.login

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.inuappcenter.gravit.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

private const val TAG = "GoogleLogin"

suspend fun loginWithGoogle(
    context: Context
): String {
    val credentialManager = CredentialManager.create(context)

    val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
        serverClientId = BuildConfig.GOOGLE_CLIENT_ID
    )
        .build()
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

    return try {
        val result: GetCredentialResponse = credentialManager.getCredential(
            request = request,
            context = context,
        )

        handleSignInWithGoogleResult(result)
    } catch (e: NoCredentialException) {
        Log.w(TAG, "No Google credentials (계정 없음 or 사용자 취소)", e)
        throw e
    } catch (e: GetCredentialException) {
        Log.e(TAG, "CredentialManager GetCredentialException", e)
        throw e
    } catch (e: Exception) {
        Log.e(TAG, "loginWithGoogle failed", e)
        throw e
    }
}

private fun handleSignInWithGoogleResult(
    result: GetCredentialResponse
): String {
    val credential: Credential = result.credential

    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return try {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)

                    val idToken = googleIdTokenCredential.idToken
                    Log.d(TAG, "Google ID Token = ${idToken.take(12)}...")

                    if (idToken.isNullOrBlank()) {
                        throw IllegalStateException("Google ID Token is null")
                    }
                    idToken
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "Invalid google id token", e)
                    throw e
                }
            } else {
                Log.e(TAG, "Unexpected CustomCredential type: ${credential.type}")
                throw IllegalStateException("Unexpected custom credential type")
            }
        }

        else -> {
            Log.e(TAG, "Unexpected credential class: ${credential.javaClass.name}")
            throw IllegalStateException("Unexpected credential type")
        }
    }
}