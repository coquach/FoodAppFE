package com.example.foodapp.ui.screen.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.models.request.SignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {

    private val _uiState = MutableStateFlow<SignUpEvent>(SignUpEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onUsernameChanged(username: String) {
        _username.value = username
    }

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onSignUpClick() {
       viewModelScope.launch {
           _uiState.value = SignUpEvent.Loading
            try {
                val response = foodApi.signUp(
                    SignUpRequest(
                        username = username.value,
                        email = email.value,
                        password = password.value
                    )
                )
                if (response.token.isNotEmpty()) {
                    _uiState.value = SignUpEvent.Success
                    _navigationEvent.emit(SignUpNavigationEvent.NavigateHome)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SignUpEvent.Error
            }

       }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.emit(SignUpNavigationEvent.NavigateLogin)
        }
    }


    sealed class  SignUpNavigationEvent {
        data object NavigateLogin: SignUpNavigationEvent()
        data object NavigateHome: SignUpNavigationEvent()
    }

    sealed class SignUpEvent {
        data object Nothing: SignUpEvent()
        data object Success: SignUpEvent()
        data object Error: SignUpEvent()
        data object Loading: SignUpEvent()
    }
}