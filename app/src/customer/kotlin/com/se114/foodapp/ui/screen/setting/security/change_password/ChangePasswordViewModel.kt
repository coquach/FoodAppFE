package com.se114.foodapp.ui.screen.setting.security.change_password


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute

import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.ResetPasswordUseCase
import com.example.foodapp.navigation.ResetPassword
import com.example.foodapp.ui.screen.components.validateField
import com.se114.foodapp.domain.use_case.user.ReAuthenticateUseCase
import com.se114.foodapp.domain.use_case.user.UpdatePasswordUseCase

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
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val reAuthenticateUseCase: ReAuthenticateUseCase,
) : ViewModel() {


    private val _uiState = MutableStateFlow(ChangePasswordState.UiState())
    val uiState: StateFlow<ChangePasswordState.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<ChangePasswordState.Event>()
    val event = _event.receiveAsFlow()


    fun validate(type: String) {
        val current = _uiState.value
        var passwordError: String? = current.newPasswordError
        var confirmPasswordError: String? = current.confirmNewPasswordError
        when (type) {


            "newPassword" -> {
                passwordError = validateField(
                    current.newPassword.trim(),
                    "Mật khẩu phải có ít nhất 6 ký tự"
                ) { it.length >= 6 }

            }

            "confirmPassword" -> {
                confirmPasswordError = validateField(
                    current.confirmNewPassword.trim(),
                    "Mật khẩu không khớp"

                ) { it == current.password }
            }

        }
        val isValid =
            current.newPassword.isNotBlank() && current.confirmNewPassword.isNotBlank() && passwordError == null && confirmPasswordError == null
        _uiState.update {
            it.copy(
                newPasswordError = passwordError,
                confirmNewPasswordError = confirmPasswordError,
                isValid = isValid
            )
        }
    }


    private fun reAuthenticate() {
        viewModelScope.launch {
            reAuthenticateUseCase(
                email = _uiState.value.email,
                password = _uiState.value.password
            ).collect { response ->
                when (response) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is FirebaseResult.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        updatePassword()
                    }

                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(isLoading = false, error = response.error) }
                        _event.send(ChangePasswordState.Event.ShowError)
                    }

                }
            }
        }
    }

    private fun updatePassword() {
        viewModelScope.launch {
            updatePasswordUseCase(newPassword = _uiState.value.newPassword).collect { response ->
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
            is ChangePasswordState.Action.OnNewPasswordChanged -> {
                _uiState.update { it.copy(newPassword = action.password) }
            }
            is ChangePasswordState.Action.OnConfirmNewPasswordChanged -> {
                _uiState.update { it.copy(confirmNewPassword = action.confirmPassword) }
            }

            ChangePasswordState.Action.ReAuthenticate -> {
                reAuthenticate()}

            is ChangePasswordState.Action.OnEmailChanged -> {
                _uiState.update { it.copy(email = action.email) }
            }
            is ChangePasswordState.Action.OnPasswordChanged -> {
                _uiState.update { it.copy(password = action.password) }
            }

        }
    }

}

object ChangePasswordState {
    data class UiState(
        val isLoading: Boolean = false,
        val isValid: Boolean = false,
        val email: String = "",
        val password: String = "",
        val newPassword: String = "",
        val confirmNewPassword: String = "",
        val newPasswordError: String? = null,
        val confirmNewPasswordError: String? = null,
        val error: String? = null,
    )

    sealed interface Event {

        data object NavigateToSuccess : Event
        data object ShowError : Event
    }

    sealed interface Action {
        data class OnNewPasswordChanged(val password: String) : Action
        data class OnConfirmNewPasswordChanged(val confirmPassword: String) : Action
        data class OnEmailChanged(val email: String) : Action
        data class OnPasswordChanged(val password: String) : Action
        data object ReAuthenticate : Action


    }
}