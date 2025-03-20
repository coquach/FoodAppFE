package com.example.foodapp.ui.screen.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.models.request.LoginRequest
import com.example.foodapp.data.models.request.SignUpRequest
import com.example.foodapp.data.remote.ApiResponse
import com.example.foodapp.data.remote.safeApiCall
import com.example.foodapp.ui.screen.auth.BaseAuthViewModel
import com.example.foodapp.ui.screen.auth.signup.SignUpViewModel.SignUpEvent
import com.example.foodapp.ui.screen.auth.signup.SignUpViewModel.SignUpNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(override val foodApi: FoodApi): BaseAuthViewModel(foodApi) {

    private val _uiState = MutableStateFlow<LoginEvent>(LoginEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()


    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onUsernameChanged(username: String) {
        _username.value = username
    }


    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onLoginClick() {
       viewModelScope.launch {
           _uiState.value = LoginEvent.Loading
           try {
               val response = safeApiCall {
                   foodApi.login(
                       LoginRequest(
                           username = username.value,
                           password = password.value
                       )
                   )
               }
               when (response) {
                   is ApiResponse.Success -> {
                       _uiState.value = LoginEvent.Success

                       _navigationEvent.emit(LoginNavigationEvent.NavigateHome)
                   }

                   else -> {
                       val err = (response as? ApiResponse.Error)?.code ?: 0
                       error = "Đăng nhập thất bại"
                       errorDescription = "Không thể đăng nhập tài khoản"
                       when (err) {
                           400 -> {
                               error = "Thông tin không hợp lệ"
                               errorDescription = "Vui lòng nhập thông tin chính xác."
                           }
                       }
                       _uiState.value = LoginEvent.Error
                   }
               }


           } catch (e: Exception) {
               e.printStackTrace()
               _uiState.value = LoginEvent.Error
           }

       }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.NavigateSignUp)
        }
    }


    sealed class  LoginNavigationEvent {
        data object NavigateSignUp: LoginNavigationEvent()
        data object NavigateHome: LoginNavigationEvent()
    }

    sealed class LoginEvent {
        data object Nothing: LoginEvent()
        data object Success: LoginEvent()
        data object Error: LoginEvent()
        data object Loading: LoginEvent()
    }
}