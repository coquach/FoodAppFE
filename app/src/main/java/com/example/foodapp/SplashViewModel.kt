package com.example.foodapp

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.NavRoute
import com.example.foodapp.navigation.Welcome
import com.example.foodapp.data.datastore.WelcomeRepository
import com.example.foodapp.data.model.Account
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.navigation.OrderList


import com.example.foodapp.navigation.Statistics
import com.example.foodapp.navigation.Tracking
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val welcomeRepository: WelcomeRepository,

    ) : ViewModel() {

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = mutableStateOf<NavRoute>(Auth)
    val startDestination = _startDestination as State<NavRoute>

    private val _navigateEventChannel = Channel<UiEvent>()
    val navigateEventFlow = _navigateEventChannel.receiveAsFlow()

    private val currentUserStateFlow = accountRepository.currentUser


    init {
        viewModelScope.launch {
            val initialUser = currentUserStateFlow.firstOrNull()
            updateStartDestination(initialUser)

            Log.d("firstOrNull: ", "done")

            currentUserStateFlow.drop(1).collect { user ->
                if (user == null) {
                    Log.d("collect: ", "done")
                    _navigateEventChannel.send(UiEvent.NavigateToAuth)
                }
            }
        }

    }

    private suspend fun updateStartDestination(user: Account?) {
        when (BuildConfig.FLAVOR) {
            "admin" -> {
                _startDestination.value = if (user == null) Auth else Statistics
            }

            "staff" -> {
                _startDestination.value = if (user == null) Auth else Home
            }

            "shipper" -> {
                _startDestination.value = if (user == null) Tracking else OrderList
            }
            else -> {
                val completed = welcomeRepository.readOnBoardingState().firstOrNull() == true
                if (completed) {
                    _startDestination.value = if (user == null) Auth else Home
                } else {
                    _startDestination.value = Welcome
                }
            }
        }
        _isLoading.value = false

    }
    sealed class UiEvent {
        data object NavigateToAuth : UiEvent()
    }
}


