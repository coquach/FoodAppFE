package com.example.foodapp.ui.screen.auth.forgot_password.send_email

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.service.AccountService
import com.example.foodapp.ui.screen.auth.BaseAuthViewModel
import com.example.foodapp.ui.screen.auth.signup.SignUpViewModel.SignUpEvent
import com.example.foodapp.utils.ValidateField
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendEmailViewModel @Inject constructor(
    private val accountService: AccountService
) : BaseAuthViewModel() {
    private val _uiState = MutableStateFlow<SendEmailState>(SendEmailState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<SendEmailEvents>();
    val event = _event.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    fun onEmailChanged(email: String) {
        _email.value = email
        isEmailSent.value = false
    }

    var emailError = mutableStateOf<String?>(null)

    private var lastClickTime = 0L

    var isEmailSent = mutableStateOf(false)
        private set


    fun validate(): Boolean {
        var isValid = true

        isValid = ValidateField(
            email.value,
            emailError,
            "Email không hợp lệ"
        ) { it.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")) } && isValid

        return isValid

    }
    fun onSendEmailClick(){
        viewModelScope.launch {


            if (isEmailSent.value) {
                _uiState.value = SendEmailState.AlreadySent
                return@launch
            }

            // Tránh spam: cooldown 3s
            if (System.currentTimeMillis() - lastClickTime < 3000) {
                _uiState.value = SendEmailState.TooFast
                return@launch
            }
            lastClickTime = System.currentTimeMillis()
            _uiState.value = SendEmailState.Loading
            try {
                if (validate()) {
                    delay(3000)
                    accountService.forgetPassword(email.value)
                    isEmailSent.value = true
                    _uiState.value = SendEmailState.Success
                }else {
                    error = "Email không hợp lệ"
                    errorDescription = "Vui lòng nhập email chính xác."
                    _uiState.value = SendEmailState.Error
                }

                _uiState.value = SendEmailState.Nothing
            }catch (e: FirebaseAuthInvalidUserException) {
                error = "Email chưa được đăng ký."
                errorDescription = "Vui lòng nhập email khác"
                _uiState.value =  SendEmailState.Error

            } catch (e: Exception) {
                error = "Lỗi không xác định"
                errorDescription = "Vui lòng thử lại"
                _uiState.value = SendEmailState.Error
            }
        }
    }



    fun onLoginClick() {
       viewModelScope.launch {
           _event.emit(SendEmailEvents.NavigateToLogin)
       }
    }




    sealed class SendEmailState {
        data object Nothing : SendEmailState()
        data object Success : SendEmailState()
        data object Error : SendEmailState()
        data object Loading : SendEmailState()
        data object AlreadySent: SendEmailState()
        data object  TooFast : SendEmailState()
    }

    sealed class SendEmailEvents{
        object NavigateToLogin : SendEmailEvents()
    }
}