package com.example.foodapp.ui.screen.auth.login

import android.util.Log
import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.LogOutUseCase
import com.example.foodapp.domain.use_case.auth.LoginByEmailUseCase
import com.example.foodapp.domain.use_case.auth.LoginByGoogleUseCase
import com.example.foodapp.ui.screen.components.validateField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginByEmailUseCase: LoginByEmailUseCase,
    private val loginByGoogleUseCase: LoginByGoogleUseCase,
    private val logOutUseCase: LogOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(Login.UiState())
    val uiState: StateFlow<Login.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<Login.Event>()
    val event = _event.receiveAsFlow()

    fun onAction(action: Login.Action) {
        when (action) {
            is Login.Action.EmailChanged -> {
                _uiState.value = _uiState.value.copy(email = action.email)
            }

            is Login.Action.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = action.password)
            }

            is Login.Action.LoginClicked -> {
                loginByEmail()
            }

            is Login.Action.SignUpClicked -> {
                viewModelScope.launch {
                    _event.send(Login.Event.NavigateSignUp)
                }
            }

            is Login.Action.ForgotPasswordClicked -> {
                viewModelScope.launch {
                    _event.send(Login.Event.NavigateForgot)
                }
            }

            is Login.Action.LoginWithGoogleClicked -> {
                loginByGoogle(action.credential)
            }

            is Login.Action.rememberLoginClicked -> {

            }

        }

    }

    fun validate(type: String) {
        val current = _uiState.value
        var emailError: String? = current.emailError
        var passwordError: String? = current.passwordError
        when (type) {
            "email" -> {
                 emailError = validateField(
                    current.email.trim(),
                    "Email không hợp lệ"
                ) { it.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) }


            }

            "password" -> {
                 passwordError = validateField(
                    current.password.trim(),
                    "Mật khẩu phải có ít nhất 6 ký tự"
                ) { it.length >= 6 }


            }

        }
        val isValid = current.email.isNotBlank() && current.password.isNotBlank() && emailError == null && passwordError == null
        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                isValid = isValid
            )
        }
    }


    private fun loginByEmail() {
        viewModelScope.launch {
            loginByEmailUseCase.invoke(_uiState.value.email, _uiState.value.password)
                .collect { result ->
                    when (result) {
                        is FirebaseResult.Loading -> {
                            _uiState.update { it.copy(loading = true) }
                        }

                        is FirebaseResult.Success -> {
                            _uiState.update { it.copy(loading = false) }
                           _event.send(Login.Event.NavigateToHome)
                        }

                        is FirebaseResult.Failure -> {
                            _uiState.update { it.copy(loading = false, error = result.error) }
                            _event.send(Login.Event.ShowError)

                        }
                    }
                }
        }

    }


    private fun loginByGoogle(credential: Credential) {
        viewModelScope.launch {
            loginByGoogleUseCase.invoke(credential).collect { result ->
                when (result) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(loading = true) }
                    }

                    is FirebaseResult.Success -> {
                        _uiState.update { it.copy(loading = false) }
                        _event.send(Login.Event.NavigateToHome)
                    }

                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(loading = false, error = result.error) }
                        _event.send(Login.Event.ShowError)
                    }
                }
            }

        }


    }
    fun logOut() {
        logOutUseCase.invoke()
    }

}


object Login {
    data class UiState(
        val email: String = "",
        val emailError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val loading: Boolean = false,
        val error: String? = null,
        val isValid: Boolean = false,
    )

    sealed interface Event {
        data object NavigateSignUp : Event
        data object NavigateToHome : Event
        data object NavigateForgot : Event
        data object ShowError : Event
    }

    sealed interface Action {
        data class EmailChanged(val email: String) : Action
        data class PasswordChanged(val password: String) : Action
        data object LoginClicked : Action
        data object SignUpClicked : Action
        data object ForgotPasswordClicked : Action
        data class LoginWithGoogleClicked(val credential: Credential) : Action
        data object rememberLoginClicked : Action
    }
}
