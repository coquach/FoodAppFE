package com.example.foodapp.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.foodapp.data.datastore.CartRepository
import com.example.foodapp.data.dto.request.RefreshTokenRequest
import com.example.foodapp.data.remote.ApiResponse
import com.example.foodapp.data.remote.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FoodAppSession @Inject constructor(
    private val context: Context,
    private val cartRepository: CartRepository
) {
    private val sharePres: SharedPreferences =
        context.getSharedPreferences("FoodApp", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val TAG = "FoodAppSession"
    }

    private val _sessionExpiredFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredFlow: SharedFlow<Unit> = _sessionExpiredFlow

    var isManualLogout = false
        private set

    fun storeToken(accessToken: String, refreshToken: String) {
        withContext(Dispatchers.IO) {
            sharePres.edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .apply()
        }
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

    fun clearTokens(manual: Boolean = false) {
        isManualLogout = manual
        sharePres.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
        CoroutineScope(Dispatchers.IO).launch {
            val allCartItems = cartRepository.getCartItems().firstOrNull() ?: emptyList()
            cartRepository.clearCartItems(allCartItems)
        }
        _sessionExpiredFlow.tryEmit(Unit)
    }

}