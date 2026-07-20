package com.inuappcenter.gravit.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GravitFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM_RECEIVE", "messageId=${message.messageId}")
        Log.d("FCM_RECEIVE", "notification=${message.notification}")
        Log.d("FCM_RECEIVE", "data=${message.data}")
    }

    @Deprecated("Deprecated in Java")
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCM_TOKEN", "새 토큰: $token")
    }
}