package com.example.foodapp.ui.screen.auth.forgot_password.send_email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.SendEmailResetUseCase
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
class SendEmailViewModel @Inject constructor(
    private val sendEmailResetUseCase: SendEmailResetUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SendEmailReset.UiState())
    val uiState: StateFlow<SendEmailReset.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<SendEmailReset.Event>()
    val event = _event.receiveAsFlow()



    fun validate(type: String) {
        val current = _uiState.value
        var emailError: String? = current.emailError
        when (type) {
            "email" -> {
                 emailError = validateField(
                    current.email.trim(),
                    "Email không hợp lệ"
                ) { it.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) }

            }

        }
        val isValid = emailError == null
        _uiState.update {
            it.copy(
                emailError = emailError,
                isValid = isValid
            )
        }
    }

    private fun sendEmail() {
        viewModelScope.launch {
            sendEmailResetUseCase(uiState.value.email).collect {response ->
                when (response) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is FirebaseResult.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(SendEmailReset.Event.ShowSuccess)
                    }
                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(isLoading = false, error = response.error) }
                        _event.send(SendEmailReset.Event.ShowError)
                    }
                }
            }
        }
    }


    fun onAction(action: SendEmailReset.Action) {
        when (action) {
            is SendEmailReset.Action.OnEmailChanged -> {
                _uiState.update { it.copy(email = action.email) }
            }
            is SendEmailReset.Action.SendEmail -> {
                    sendEmail()
            }
            is SendEmailReset.Action.Login -> {
                viewModelScope.launch {
                    _event.send(SendEmailReset.Event.NavigateToLogin)
                }
            }
    }



}
}

object SendEmailReset {
    data class UiState(
        val isLoading: Boolean = false,
        val isValid: Boolean = false,
        val email: String = "",
        val emailError: String? = null,
        val error: String? = null,
    )

    sealed interface Event {
        data object NavigateToLogin : Event
        data object ShowSuccess : Event
        data object ShowError : Event
    }

    sealed interface Action {
        data class OnEmailChanged(val email: String) : Action
        data object SendEmail : Action
        data object Login : Action
    }
}