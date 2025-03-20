package com.example.foodapp.data

import android.content.Context
import android.content.SharedPreferences

class FoodAppSession(private val context: Context) {
    val sharePres: SharedPreferences =
        context.getSharedPreferences("FoodApp", Context.MODE_PRIVATE)


    fun storeToken(token: String) {
        sharePres.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        sharePres.getString("token", "")?.let {
            return it
        }
        return null
    }
}