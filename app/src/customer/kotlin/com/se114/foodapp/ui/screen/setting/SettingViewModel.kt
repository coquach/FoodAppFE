package com.se114.foodapp.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.data.remote.ApiResponse
import com.example.foodapp.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val foodAppSession: FoodAppSession
) : ViewModel() {

    private val _event = MutableSharedFlow<SettingEvents>()
    val event = _event.asSharedFlow()

    fun onLogoutClicked() {
        viewModelScope.launch {
            try {
                val token = foodAppSession.getAccessToken()
                if (token.isNullOrEmpty()) {
                    return@launch
                }

                val response = safeApiCall {
                    foodApi.logout(token)
                }
                when (response) {
                    is ApiResponse.Success -> {
                        foodAppSession.clearTokens(manual = true)
                        _event.emit(SettingEvents.Logout)
                    }
                    else -> {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    sealed class SettingEvents {
        data object Logout : SettingEvents()

    }
}