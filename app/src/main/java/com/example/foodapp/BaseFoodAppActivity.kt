package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.foodapp.data.model.Order
import com.example.foodapp.ui.screen.notification.NotificationState
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.google.gson.Gson

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFoodAppActivity : ComponentActivity() {

    val viewModel by viewModels<MainViewModel>()
    val notificationViewModel by viewModels<NotificationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        notificationViewModel.onAction(NotificationState.Action.Retry)
        Log.d("INTENT_CHECK", "Intent extras: ${intent.extras}")
        processIntent(intent)
    }

    private fun processIntent(intent: Intent) {
        if (intent.hasExtra("type")) {
            viewModel.navigateToNotification()
            intent.removeExtra("type")
        }
        intent.data?.let { uri ->
            viewModel.handleDeeplink(uri)
        }
    }
}