package com.example.foodapp.ui.screen.auth.forgot_password.change_password


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute

import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.ResetPasswordUseCase
import com.example.foodapp.navigation.ResetPassword
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
class ChangePasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val navArgs = savedStateHandle.toRoute<ResetPassword>()
    private val _oobCode = navArgs.oobCode

    private val _uiState = MutableStateFlow(ChangePasswordState.UiState())
    val uiState: StateFlow<ChangePasswordState.UiState>  get()= _uiState.asStateFlow()

    private val _event = Channel<ChangePasswordState.Event>()
    val event = _event.receiveAsFlow()



    fun validate(type: String) {
        val current = _uiState.value
        var passwordError: String? = current.passwordError
        var confirmPasswordError: String? = current.confirmPasswordError
        when (type) {


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
        val isValid =  passwordError == null && confirmPasswordError == null
        _uiState.update {
            it.copy(
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                isValid = isValid
            )
        }
    }



    private fun resetPassword() {
        viewModelScope.launch {
            resetPasswordUseCase(oobCode = _oobCode, newPassword = _uiState.value.password).collect { response ->
                when (response) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is FirebaseResult.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(ChangePasswordState.Event.NavigateToSuccess)

                    }
                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(isLoading = false, error = response.error) }
                        _event.send(ChangePasswordState.Event.ShowError)
                    }

                }
            }

        }

    }
    fun onAction(action: ChangePasswordState.Action) {
        when (action) {
            is ChangePasswordState.Action.OnPasswordChanged -> {
                _uiState.update { it.copy(password = action.password) }
            }
            is ChangePasswordState.Action.OnConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = action.confirmPassword) }
            }

            is ChangePasswordState.Action.ResetPassword -> {

                    resetPassword()

            }
            is ChangePasswordState.Action.Auth -> {
                viewModelScope.launch {
                    _event.send(ChangePasswordState.Event.NavigateToAuth)
                }
            }
        }
    }

}

object ChangePasswordState {
    data class UiState(
        val isLoading: Boolean = false,
        val isValid: Boolean = false,
        val password: String = "",
        val confirmPassword: String = "",
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val error: String? = null,
    )

    sealed interface Event{
        data object NavigateToAuth : Event
        data object NavigateToSuccess : Event
        data object ShowError : Event
    }
    sealed interface Action{
        data class OnPasswordChanged(val password: String) : Action
        data class OnConfirmPasswordChanged(val confirmPassword: String) : Action
        data object ResetPassword : Action
        data object Auth : Action

    }
}