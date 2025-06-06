package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.foodapp.data.model.Order
import com.google.gson.Gson

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFoodAppActivity : ComponentActivity(){

    val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processIntent(intent)
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("INTENT_CHECK", "Intent extras: ${intent.extras}")
        processIntent(intent)
    }

    private fun processIntent(intent: Intent) {
        if (intent.hasExtra("order_data")) {
            val orderJson = intent.getStringExtra("order_data")
            if (!orderJson.isNullOrBlank()) {
                val gson = Gson()
                val order = gson.fromJson(orderJson, Order::class.java)
                viewModel.navigateToOrderDetail(order)
            }
            intent.removeExtra("order_data")
        }
        intent.data?.let { uri ->
            viewModel.handleDeeplink(uri)
        }
    }
}