package com.se114.foodapp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.NavRoute
import com.example.foodapp.ui.navigation.Welcome
import com.se114.foodapp.data_store.WelcomeRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val foodAppSession: FoodAppSession,
    private val welcomeRepository: WelcomeRepository
) : ViewModel() {

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<NavRoute> = mutableStateOf(Welcome)
    val startDestination: State<NavRoute> = _startDestination

    private val _showSessionExpiredDialog: MutableState<Boolean> = mutableStateOf(false)
    val showSessionExpiredDialog: State<Boolean> = _showSessionExpiredDialog


    init {
        viewModelScope.launch {
            welcomeRepository.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value = if (foodAppSession.getRefreshToken().isNullOrEmpty()) Auth else Home
                } else {
                    _startDestination.value = Welcome
                }
            }
            _isLoading.value = false
        }
        viewModelScope.launch {
            foodAppSession.sessionExpiredFlow.collectLatest {
                if (!foodAppSession.isManualLogout) {
                    _showSessionExpiredDialog.value = true
                }
            }
        }
    }
    fun dismissSessionExpiredDialog() {
        _showSessionExpiredDialog.value = false
    }

}