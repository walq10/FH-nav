package com.fhnav.app.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send token to Supabase for push notifications
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // Handle incoming push notifications
        message.notification?.let { notification ->
            val title = notification.title ?: "FH 导航"
            val body = notification.body ?: ""
            // TODO: Show notification based on type
        }

        message.data.let { data ->
            when (data["type"]) {
                "friend_request" -> {
                    // Handle friend request notification
                }
                "phrase_message" -> {
                    // Handle phrase message notification
                }
                "location_update" -> {
                    // Handle location update notification
                }
            }
        }
    }
}
