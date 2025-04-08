package com.example.foodapp.ui.screen.auth.forgot_password.change_password

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.data.service.AccountService
import com.example.foodapp.ui.screen.auth.BaseAuthViewModel
import com.example.foodapp.ui.screen.auth.signup.SignUpViewModel.SignUpEvent
import com.example.foodapp.utils.ValidateField
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val accountService: AccountService
) : BaseAuthViewModel() {
    private val _uiState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<ChangePasswordEvents>()
    val event = _event.asSharedFlow()


    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    var passwordError = mutableStateOf<String?>(null)
    var confirmPasswordError = mutableStateOf<String?>(null)

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    private val _oobCode = mutableStateOf<String?>(null)
    private val _mode = mutableStateOf<String?>(null)

    fun validate(): Boolean {
        var isValid = true

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

    fun setResetPasswordArgs(oobCode: String, mode: String) {
        _oobCode.value = oobCode
        _mode.value = mode
    }

    fun resetPassword() {
        viewModelScope.launch {
            val oobCode = _oobCode.value
            if (oobCode == null) {
                error = "Lỗi mã xác thực"
                errorDescription = "Mã xác thực không hợp lệ hoặc bị thiếu. Vui lòng thử lại."
                _uiState.value = ChangePasswordState.Error
                return@launch
            }

            _uiState.value = ChangePasswordState.Loading
            try {
                if (validate()) {
                    delay(3000)
                    accountService.resetPassword(oobCode, password.value)
                    _uiState.value = ChangePasswordState.Success
                    _event.emit(ChangePasswordEvents.NavigateToSuccess)
                }else {
                    error = "Mật khẩu không hợp lệ"
                    errorDescription = "Vui lòng nhập mật khẩu chính xác."
                    _uiState.value = ChangePasswordState.Error
                }

            } catch (e: FirebaseAuthException) {
                error = "Lỗi xác thực"
                errorDescription = e.localizedMessage ?: "Đã xảy ra lỗi trong quá trình tạo lại mật khẩu."
                _uiState.value = ChangePasswordState.Error

            } catch (e: Exception) {
                e.printStackTrace()
                error = "Lỗi không xác định"
                errorDescription = e.localizedMessage ?: "Vui lòng thử lại sau."
                _uiState.value = ChangePasswordState.Error
            }
        }

    }
    sealed class ChangePasswordState {
        data object Nothing : ChangePasswordState()
        data object Success : ChangePasswordState()
        data object Error : ChangePasswordState()
        data object Loading : ChangePasswordState()

    }

    sealed class ChangePasswordEvents{
        object NavigateToSuccess : ChangePasswordEvents()
    }
}