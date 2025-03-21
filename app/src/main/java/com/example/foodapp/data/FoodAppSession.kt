package com.example.foodapp.data

import android.content.Context
import android.content.SharedPreferences

class FoodAppSession(private val context: Context) {
    val sharePres: SharedPreferences =
        context.getSharedPreferences("FoodApp", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    fun storeToken(accessToken: String, refreshToken: String) {
        sharePres.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getAccessToken(): String? {
        sharePres.getString(KEY_ACCESS_TOKEN, "")?.let {
            return it
        }
        return null
    }

    fun getRefreshToken(): String? {
        sharePres.getString(KEY_REFRESH_TOKEN, "")?.let {
            return it
        }
        return null
    }
    fun clearTokens() {
        sharePres.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }
}