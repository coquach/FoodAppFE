package com.example.foodapp.notification

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.example.foodapp.data.model.Order
import com.se114.foodapp.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FoodAppMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var foodAppNotificationManager: FoodAppNotificationManager
    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Nhận thông báo từ: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Title: ${it.title}")
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }

        remoteMessage.data.let {
            Log.d("FCM", "Message Data: $it")
        }

        val title = remoteMessage.notification?.title ?: "Thông báo"
        val messageText = remoteMessage.notification?.body ?: "Có gì đó mới!"
        val data = remoteMessage.data
        val type = data["type"] ?: "order"
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("type", type)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationChannelType: FoodAppNotificationManager.NotificationChannelType = when (type) {
            "order" -> FoodAppNotificationManager.NotificationChannelType.ORDER

            else -> {
                FoodAppNotificationManager.NotificationChannelType.ORDER
            }
        }
        val notificationID: Int = when (notificationChannelType) {
            FoodAppNotificationManager.NotificationChannelType.ORDER ->  20000 + (0..999).random()
            FoodAppNotificationManager.NotificationChannelType.DELIVERY -> 30000 + (0..999).random()

        }
        foodAppNotificationManager.showNotification(
            title, messageText, notificationID, pendingIntent,
            notificationChannelType)

    }
}