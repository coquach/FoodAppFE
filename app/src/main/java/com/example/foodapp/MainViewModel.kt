package com.example.foodapp

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.datastore.WelcomeRepository
import com.example.foodapp.data.model.Account
import com.example.foodapp.data.model.Order
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.Home
import com.example.foodapp.navigation.NavRoute
import com.example.foodapp.navigation.Welcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val welcomeRepository: WelcomeRepository,
) : ViewModel() {

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    private val _startDestination: MutableState<NavRoute?> = mutableStateOf(null)
    val startDestination: State<NavRoute?> = _startDestination


    private val currentUserStateFlow = accountRepository.currentUser

    init {
        viewModelScope.launch {
            val initialUser = currentUserStateFlow.firstOrNull()
            updateStartDestination(initialUser)

            Log.d("firstOrNull: ", "done")
            if (initialUser !=null){
                currentUserStateFlow.drop(1).collect { user ->
                    if (user == null) {
                        Log.d("collect: ", "done")
                        _event.emit(UiEvent.NavigateToAuth)
                    }
                }
            }

        }

    }

    private suspend fun updateStartDestination(user: Account?) {
        when (BuildConfig.FLAVOR) {
            "customer" -> {

                val completed = welcomeRepository.readOnBoardingState().firstOrNull() == true
                if (completed) {
                    _startDestination.value = if (user == null) Auth else Home
                } else {
                    _startDestination.value = Welcome
                }
            }
            else -> {
                if(user == null)Auth else Home
            }
        }

    }



    fun navigateToNotification() {
        viewModelScope.launch {
            _event.emit(UiEvent.NavigateToNotification)
        }
    }

    sealed class UiEvent {
        data object NavigateToNotification: UiEvent()
        data object NavigateToAuth : UiEvent()

    }
}