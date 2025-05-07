package com.example.foodapp.ui.screen.auth.signup

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope


import com.example.foodapp.BaseViewModel

import com.example.foodapp.data.service.AccountService
import com.example.foodapp.utils.ValidateField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,

) : BaseViewModel() {

    private val _uiState = MutableStateFlow<SignUpState>(SignUpState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<SignUpEvent>()
    val event = _event.receiveAsFlow()


    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()


    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }


    var emailError = mutableStateOf<String?>(null)
    var passwordError = mutableStateOf<String?>(null)
    var confirmPasswordError = mutableStateOf<String?>(null)

    private fun validate(): Boolean {
        var isValid = true

        isValid = ValidateField(
            email.value,
            emailError,
            "Email không hợp lệ"
        ) { it.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")) } && isValid

        isValid = ValidateField(
            password.value,
            passwordError,
            "Mật khẩu phải có ít nhất 6 ký tự"
        ) { it.length >= 6 } && isValid

        isValid = ValidateField(
            confirmPassword.value,
            confirmPasswordError,
            "Mật khẩu không trùng khớp"
        ) { it == password.value } && isValid

        return isValid
    }


    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.value = SignUpState.Loading


                try {
                    if (!validate()) {
                        throw IllegalArgumentException("Vui lòng nhập thông tin chính xác.")
                    }

                    accountService.createAccountWithEmail(email.value, password.value)
                    _uiState.value = SignUpState.Success

                    _event.send(SignUpEvent.NavigateProfile)

                } catch (e: IllegalArgumentException){
                    error = "Thông tin không hợp lệ"
                    errorDescription = e.message ?: "Có gì đó sai sai"
                    _uiState.value = SignUpState.Error
                }

                catch (e: Exception) {
                    e.printStackTrace()
                    error = "Lỗi không xác định"
                    errorDescription = e.message ?: "Có gì đó sai sai"
                    _uiState.value = SignUpState.Error
                }

            }


        }



    fun onLoginClick() {
        viewModelScope.launch {
            _event.send(SignUpEvent.NavigateLogin)
        }
    }



    sealed class SignUpEvent {
        data object NavigateLogin : SignUpEvent()
        data object NavigateProfile : SignUpEvent()
    }

    sealed class SignUpState {
        data object Nothing : SignUpState()
        data object Success : SignUpState()
        data object Error : SignUpState()
        data object Loading : SignUpState()
    }
}