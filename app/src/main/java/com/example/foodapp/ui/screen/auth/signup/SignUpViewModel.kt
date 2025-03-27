package com.example.foodapp.ui.screen.auth.signup

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope

import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.data.dto.request.SignUpRequest
import com.example.foodapp.data.remote.ApiResponse
import com.example.foodapp.data.remote.safeApiCall
import com.example.foodapp.ui.screen.auth.BaseAuthViewModel
import com.example.foodapp.utils.ValidateField
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
    override val foodApi: FoodApi,
    val session: FoodAppSession
) : BaseAuthViewModel(foodApi) {

    private val _uiState = MutableStateFlow<SignUpEvent>(SignUpEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _fullName = MutableStateFlow("")
    val fullName = _fullName.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    fun onUsernameChanged(username: String) {
        _username.value = username
    }

    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onFullNameChanged(fullName: String) {
        _fullName.value = fullName
    }

    fun onPhoneNumberChanged(phoneNumber: String) {
        _phoneNumber.value = phoneNumber
    }

    var fullNameError = mutableStateOf<String?>(null)
    var usernameError = mutableStateOf<String?>(null)
    var emailError = mutableStateOf<String?>(null)
    var passwordError = mutableStateOf<String?>(null)
    var phoneNumberError = mutableStateOf<String?>(null)

    fun validate(): Boolean {
        var isValid = true

        isValid = ValidateField(
            fullName.value,
            fullNameError,
            "Họ tên không được để trống"
        ) { it.isNotBlank() } && isValid

        isValid = ValidateField(
            username.value,
            usernameError,
            "Tên đăng nhập phải có ít nhất 4 ký tự"
        ) { it.length >= 4 } && isValid

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
            phoneNumber.value,
            phoneNumberError,
            "Số điện thoại không hợp lệ"
        ) { it.matches(Regex("^(\\+84|0)[0-9]{9,10}\$")) } && isValid

        return isValid
    }


    fun onSignUpClick() {
        viewModelScope.launch {
            _uiState.value = SignUpEvent.Loading
            delay(300)
            try {
                if(validate()) {
                    val response = safeApiCall {
                        foodApi.signUp(
                            SignUpRequest(
                                fullName = fullName.value,
                                username = username.value,
                                email = email.value,
                                password = password.value,
                                phoneNumber = phoneNumber.value
                            )
                        )
                    }
                    when (response) {
                        is ApiResponse.Success -> {

                            _uiState.value = SignUpEvent.Success

                            _navigationEvent.emit(SignUpNavigationEvent.NavigateHome)
                        }

                        else -> {
                            val err = (response as? ApiResponse.Error)?.code ?: 0
                            error = "Đăng kí thất bại"
                            errorDescription = "Không thể đăng ký tài khoản"
                            when (err) {
                                400 -> {
                                    error = "Thông tin không hợp lệ"
                                    errorDescription = "Vui lòng nhập thông tin chính xác."
                                }
                            }
                            _uiState.value = SignUpEvent.Error
                        }
                    }
                }
                else {
                    error = "Thông tin không hợp lệ"
                    errorDescription = "Vui lòng nhập thông tin chính xác."
                    _uiState.value = SignUpEvent.Error
                }


            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SignUpEvent.Error
            }

        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _navigationEvent.emit(SignUpNavigationEvent.NavigateLogin)
        }
    }


    sealed class SignUpNavigationEvent {
        data object NavigateLogin : SignUpNavigationEvent()
        data object NavigateHome : SignUpNavigationEvent()
    }

    sealed class SignUpEvent {
        data object Nothing : SignUpEvent()
        data object Success : SignUpEvent()
        data object Error : SignUpEvent()
        data object Loading : SignUpEvent()
    }
}