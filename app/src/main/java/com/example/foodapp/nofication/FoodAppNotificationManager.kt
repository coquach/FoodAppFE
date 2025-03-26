package com.example.foodapp.nofication

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodAppNotificationManager @Inject constructor(
    @ApplicationContext val context: Context
)  {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val job = CoroutineScope(Dispatchers.IO + SupervisorJob())

    enum class NotificationChannelType(
        val id: String,
        val channelName: String,
        val channelDesc: String,
        val importance: Int
    ) {
        ORDER("1", "Order", "Order", NotificationManager.IMPORTANCE_HIGH),
        PROMOTION("2", "Promotion", "Promotion", NotificationManager.IMPORTANCE_DEFAULT),
        ACCOUNT("3", "Account", "Account", NotificationManager.IMPORTANCE_LOW)
    }


    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            NotificationChannelType.entries.forEach {
                val channel = NotificationChannel(it.id, it.channelName, it.importance).apply {
                    description = it.channelDesc
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun getAndStoreToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                updateToken(it.result)
            }
        }
    }

    fun updateToken(token: String) {

        job.launch {
            Log.d("FCM", "token: $token")
        }
    }

    fun showNotification(
        title:String,
        message:String,
        notificationID:Int,
        intent: PendingIntent,
        notificationChannelType: NotificationChannelType
    ){
        Log.d("Notification", "showNotification() được gọi với ID: $notificationID")
        val notification = NotificationCompat.Builder(context,notificationChannelType.id)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentIntent(intent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("FCM", "Thiếu quyền POST_NOTIFICATIONS, không thể gửi notification!")
            return
        }
        Log.d("Notification", "Gửi notification với ID: $notificationID")
        notificationManager.notify(notificationID, notification)
    }

}