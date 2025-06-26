package com.example.foodapp.ui.screen.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.RegisterUseCase
import com.example.foodapp.ui.screen.components.validateField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,

    ) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUp.UiState())
    val uiState: StateFlow<SignUp.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<SignUp.Event>()
    val event = _event.receiveAsFlow()


    fun onAction(action: SignUp.Action) {
        when (action) {
            is SignUp.Action.EmailChanged -> {
                _uiState.value = _uiState.value.copy(email = action.email)
            }

            is SignUp.Action.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = action.password)
            }

            is SignUp.Action.ConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(confirmPassword = action.confirmPassword)
            }

            is SignUp.Action.LoginClicked -> {
                viewModelScope.launch {
                    _event.send(SignUp.Event.NavigateToLogin)
                }
            }

            is SignUp.Action.SignUpClicked -> {
                signUp()
            }
        }

    }

    fun validate(type: String) {
        val current = _uiState.value
        var emailError: String? = current.emailError
        var passwordError: String? = current.passwordError
        var confirmPasswordError: String? = current.confirmPasswordError
        when (type) {
            "email" -> {
                emailError = validateField(
                    current.email.trim(),
                    "Email không hợp lệ"
                ) { it.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) }


            }

            "password" -> {
                passwordError = validateField(
                    current.password.trim(),
                    "Mật khẩu phải có ít nhất 6 ký tự"
                ) { it.length >= 6 }

            }

            "confirmPassword" -> {
                confirmPasswordError = validateField(
                    current.confirmPassword.trim(),
                    "Mật khẩu không khớp"

                ) { it == current.password }
            }

        }
        val isValid = current.email.isNotBlank() && current.password.isNotBlank() && current.confirmPassword.isNotBlank() && emailError == null && passwordError == null && confirmPasswordError == null
        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                isValid = isValid
            )
        }
    }


    private fun signUp() { // No longer returns the Flow directly if you handle it here
        viewModelScope.launch {
            registerUseCase.invoke(_uiState.value.email, _uiState.value.password)
                .collect { result ->
                    when (result) {
                        is FirebaseResult.Loading -> {
                            _uiState.update { it.copy(loading = true) }
                        }
                        is FirebaseResult.Success -> {
                            _uiState.update { it.copy(loading = false) }
                            _event.send(SignUp.Event.NavigateToProfile)
                        }
                        is FirebaseResult.Failure -> {
                            _uiState.update { it.copy(loading = false, error = result.error) }
                            _event.send(SignUp.Event.ShowError)
                        }
                    }
                }
        }
    }

}

object SignUp {
    data class UiState(
        val email: String = "",
        val emailError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val confirmPassword: String = "",
        val confirmPasswordError: String? = null,
        val loading: Boolean = false,
        val error: String? = null,
        val isValid: Boolean = false,
    )

    sealed interface Event {
        data object NavigateToLogin : Event
        data object NavigateToProfile : Event
        data object ShowError: Event
    }

    sealed interface Action {
        data class EmailChanged(val email: String) : Action
        data class PasswordChanged(val password: String) : Action
        data class ConfirmPasswordChanged(val confirmPassword: String) : Action
        data object LoginClicked : Action
        data object SignUpClicked : Action
   

    }
}

