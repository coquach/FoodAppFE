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
        foodAppNotificationManager.updateToken(token)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Nhận thông báo từ: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Title: ${it.title}")
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }

        remoteMessage.data?.let {
            Log.d("FCM", "Message Data: $it")
        }
        val intent = Intent(this, MainActivity::class.java)
        val title = remoteMessage.notification?.title ?: ""
        val messageText = remoteMessage.notification?.body ?: ""
        val data = remoteMessage.data
        val type = data["type"] ?: "general"

        if (type == "order") {
            val gson = Gson()
            val orderJson = gson.toJson(Order)
            intent.putExtra("order_data", orderJson)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationChannelType = when (type) {
            "order" -> FoodAppNotificationManager.NotificationChannelType.ORDER
            "general" -> FoodAppNotificationManager.NotificationChannelType.PROMOTION
            else -> FoodAppNotificationManager.NotificationChannelType.ACCOUNT
        }
        val notificationID = when (notificationChannelType) {
            FoodAppNotificationManager.NotificationChannelType.ORDER -> 20000 + (0..9999).random()
            FoodAppNotificationManager.NotificationChannelType.PROMOTION -> 30000 + (0..9999).random()
            FoodAppNotificationManager.NotificationChannelType.ACCOUNT -> 40000 + (0..9999).random()
        }
        foodAppNotificationManager.showNotification(
            title, messageText, notificationID, pendingIntent,
            notificationChannelType)
    }
    companion object {
        const val ORDER_ID = "orderId"
    }
}