package com.se114.foodapp

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.service.AccountService
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.NavRoute
import com.example.foodapp.ui.navigation.Welcome
import com.se114.foodapp.data_store.WelcomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val accountService: AccountService,
    private val welcomeRepository: WelcomeRepository
) : ViewModel() {

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<NavRoute> = mutableStateOf(Welcome)
    val startDestination: State<NavRoute> = _startDestination

    init {
        viewModelScope.launch {
            welcomeRepository.readOnBoardingState().collect { completed ->
                if (completed) {

                    accountService.currentUser.collect { user ->
                        _startDestination.value = if (user == null) Auth else Home
                        _isLoading.value = false
                    }
                } else {
                    _startDestination.value = Welcome
                    _isLoading.value = false
                }

            }

        }


    }
}