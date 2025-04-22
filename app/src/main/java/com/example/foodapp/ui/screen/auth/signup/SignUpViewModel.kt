package com.example.foodapp.ui.screen.auth.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope

import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.dto.request.SignUpRequest
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.service.AccountService
import com.example.foodapp.ui.screen.auth.BaseAuthViewModel
import com.example.foodapp.utils.ValidateField
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
   private val accountService: AccountService
) : BaseAuthViewModel() {

    private val _uiState = MutableStateFlow<SignUpState>(SignUpState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<SignUpEvent>()
    val event = _event.asSharedFlow()



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

    fun validate(): Boolean {
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
                if (validate()) {
                    accountService.createAccountWithEmail(email.value, password.value)
                    _event.emit(SignUpEvent.ShowSuccessDialog)
                    _uiState.value = SignUpState.Success
                } else {
                    error = "Thông tin không hợp lệ"
                    errorDescription = "Vui lòng nhập thông tin chính xác."
                    _uiState.value = SignUpState.Error
                }

            } catch (e: FirebaseAuthUserCollisionException) {
                // Email đã tồn tại
                error = "Tài khoản đã tồn tại"
                errorDescription = "Email đã được đăng ký, vui lòng đăng kí bằng email khác."
                _uiState.value = SignUpState.Error

            } catch (e: FirebaseAuthException) {
                // Các lỗi xác thực khác
                error = "Lỗi xác thực"
                errorDescription = e.localizedMessage ?: "Không thể tạo tài khoản."
                _uiState.value = SignUpState.Error

            } catch (e: Exception) {
                e.printStackTrace()
                error = "Lỗi không xác định"
                errorDescription = e.localizedMessage ?: "Vui lòng thử lại sau."
                _uiState.value = SignUpState.Error
            }

        }
    }


    fun onLoginClick() {
        viewModelScope.launch {
            _event.emit(SignUpEvent.NavigateLogin)
        }
    }


    sealed class SignUpEvent {
        data object NavigateLogin : SignUpEvent()
        data object NavigateHome : SignUpEvent()
        data object ShowSuccessDialog: SignUpEvent()
        data object ShowError : SignUpEvent()
    }

    sealed class SignUpState {
        data object Nothing : SignUpState()
        data object Success : SignUpState()
        data object Error : SignUpState()
        data object Loading : SignUpState()
    }
}