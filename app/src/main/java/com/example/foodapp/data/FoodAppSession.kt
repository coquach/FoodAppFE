package com.example.foodapp.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.foodapp.data.dto.request.RefreshTokenRequest
import com.example.foodapp.data.remote.ApiResponse
import com.example.foodapp.data.remote.safeApiCall
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class FoodAppSession @Inject constructor(private val context: Context) {
    private val sharePres: SharedPreferences =
        context.getSharedPreferences("FoodApp", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val TAG = "FoodAppSession"
    }
    private val _sessionExpiredFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredFlow: SharedFlow<Unit> = _sessionExpiredFlow

    fun storeToken(accessToken: String, refreshToken: String) {
        sharePres.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getAccessToken(): String? {
        sharePres.getString(KEY_ACCESS_TOKEN, "")?.let {
            Log.d(TAG, "Retrieved Access Token: $it")
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
        _sessionExpiredFlow.tryEmit(Unit)
    }

}