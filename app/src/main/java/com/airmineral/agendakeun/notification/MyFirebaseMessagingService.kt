package com.airmineral.agendakeun.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        const val TAG = "PushNotificationService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Dikirim dari: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if (!remoteMessage.data.isNullOrEmpty()) {
                val title: String = remoteMessage.data["title"].toString()
                val msg: String = remoteMessage.data["message"].toString()
                sendNotification(this, title, msg)
            }
        }
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification title : ${it.title}, body : ${it.body}")
            sendNotification(this, it.title, it.body)
        }
    }
}