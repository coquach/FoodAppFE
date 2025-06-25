package com.se114.foodapp.ui.screen.setting.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.ui.screen.components.validateField
import com.se114.foodapp.domain.use_case.user.CheckGoogleVerifyUseCase
import com.se114.foodapp.domain.use_case.user.CheckVerifyEmailUseCase
import com.se114.foodapp.domain.use_case.user.SendEmailVerifyUseCase
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
class SecurityViewModel @Inject constructor(
    private val checkVerifyEmailUseCase: CheckVerifyEmailUseCase,
    private val sendEmailVerifyUseCase: SendEmailVerifyUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val checkGoogleVerifyUseCase: CheckGoogleVerifyUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SecurityState.UiState(

        )
    )
    val uiState: StateFlow<SecurityState.UiState> = _uiState.asStateFlow()

    private val _event = Channel<SecurityState.Event>()
    val event = _event.receiveAsFlow()

    fun checkGoogleVerify() {
        _uiState.update {
            it.copy(isGoogleVerified = checkGoogleVerifyUseCase())
        }
    }

    fun checkVerifyEmail() {
        _uiState.update {
            it.copy(isVerifyEmail = checkVerifyEmailUseCase())
        }
    }

    private fun sendEmailVerify() {
        viewModelScope.launch {
            sendEmailVerifyUseCase().collect { result ->
                when (result) {
                    is FirebaseResult.Loading -> {
                        _uiState.update {
                            it.copy(isLoadings = true)
                        }
                    }

                    is FirebaseResult.Success -> {
                        _uiState.update {
                            it.copy(isLoadings = false)
                        }
                        _event.send(SecurityState.Event.ShowToast("Vui lòng kiểm tra email của bạn"))
                    }

                    is FirebaseResult.Failure -> {
                        _uiState.update {
                            it.copy(isLoadings = false, error = result.error)
                        }
                        _event.send(SecurityState.Event.ShowError)
                    }
                }
            }
        }
    }
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
        val isValid =  current.password.isNotBlank() && current.confirmPassword.isNotBlank() && passwordError == null && confirmPasswordError == null
        _uiState.update {
            it.copy(
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                isValid = isValid
            )
        }
    }


    fun onAction(action: SecurityState.Action) {
        when (action) {
            SecurityState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(SecurityState.Event.OnBack)
                }
            }



            SecurityState.Action.OnVerifyEmail -> {
                sendEmailVerify()
            }

            SecurityState.Action.NavigateToChangePassword -> {
                viewModelScope.launch {
                    _event.send(SecurityState.Event.NavigateToChangePassword)
                }
            }
        }
    }
}

object SecurityState {
    data class UiState(
        val isGoogleVerified: Boolean = false,
        val isLoadings: Boolean = false,
        val error: String? = null,
        val isVerifyEmail: Boolean = false,
        val password: String = "",
        val confirmPassword: String = "",
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val isValid: Boolean = false,
    )

    sealed interface Event {
        data object OnBack : Event
        data class ShowToast(val message: String) : Event
        data object ShowError : Event
        data object NavigateToChangePassword : Event

    }

    sealed interface Action {
        data object OnBack : Action
        data object OnVerifyEmail : Action
        data object NavigateToChangePassword : Action

    }
}