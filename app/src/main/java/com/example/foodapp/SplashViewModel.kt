package com.example.foodapp

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.foodapp.data.service.AccountService
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.NavRoute
import com.example.foodapp.ui.navigation.Welcome
import com.example.foodapp.data.datastore.WelcomeRepository
import com.example.foodapp.data.model.Account
import com.example.foodapp.ui.navigation.AddAddress


import com.example.foodapp.ui.navigation.Statistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow

import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
    private val welcomeRepository: WelcomeRepository,

    ) : ViewModel() {

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination: MutableState<NavRoute> = mutableStateOf(Auth)
    val startDestination: State<NavRoute> = _startDestination

    private val _navigateEventChannel = Channel<UiEvent>()
    val navigateEventFlow = _navigateEventChannel.receiveAsFlow()

    private val currentUserStateFlow = accountService.currentUser


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


