package com.example.foodapp.ui.screen.auth.login

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.viewModelScope

import com.example.foodapp.data.service.AccountService
import com.example.foodapp.BaseViewModel
import com.example.foodapp.BuildConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<LoginEvent>(LoginEvent.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()


    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    fun onEmailChanged(username: String) {
        _email.value = username
    }


    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.value = LoginEvent.Loading
            try {
                accountService.signInWithEmail(email.value, password.value)
                val role = accountService.getUserRole() ?: "customer" // fallback

                val currentFlavor = BuildConfig.FLAVOR
                if (role != currentFlavor) {
                    accountService.signOut()
                    error = "Sai tài khoản"
                    errorDescription = "Bạn đang dùng app dành cho '$currentFlavor' nhưng tài khoản này là '$role'."
                    _uiState.value = LoginEvent.Error
                    return@launch
                }

                _uiState.value = LoginEvent.Success
                when (role) {
                    "admin" -> _navigationEvent.emit(LoginNavigationEvent.NavigateToAdmin)
                    "staff" -> _navigationEvent.emit(LoginNavigationEvent.NavigateToStaff)
                    "customer" -> _navigationEvent.emit(LoginNavigationEvent.NavigateToCustomer)
                }




            } catch (e: FirebaseAuthInvalidUserException) {
                error = "Email không tồn tại"
                errorDescription = "Tài khoản chưa được đăng ký."
                _uiState.value = LoginEvent.Error

            } catch (e: FirebaseAuthInvalidCredentialsException) {
                error = "Sai mật khẩu"
                errorDescription = "Mật khẩu không đúng. Vui lòng thử lại."
                _uiState.value = LoginEvent.Error

            } catch (e: FirebaseAuthException) {
                error = "Lỗi xác thực"
                errorDescription = e.localizedMessage ?: "Lỗi không xác định từ Firebase."
                _uiState.value = LoginEvent.Error

            } catch (e: Exception) {
                e.printStackTrace()
                error = "Lỗi không xác định"
                errorDescription = e.localizedMessage ?: "Vui lòng thử lại."
                _uiState.value = LoginEvent.Error
            }

        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.NavigateSignUp)
        }
    }

    fun onForgotPasswordClick() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavigationEvent.NavigateForgot)
        }
    }

    fun onLoginWithGoogleClick(credential: Credential) {
        viewModelScope.launch {
            try {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                    _navigationEvent.emit(LoginNavigationEvent.NavigateToCustomer)

                } else throw IllegalArgumentException("Thông tin không hợp lệ")
            } catch (e: ApiException) {
                error = "Đăng nhập thất bại"
                errorDescription = e.localizedMessage ?: "Vui lòng thử lại."
                _uiState.value = LoginEvent.Error
            } catch (e: FirebaseAuthException) {

                error = "Lỗi xác thực với Firebase"
                errorDescription = e.localizedMessage ?: "Vui lòng thử lại."
                _uiState.value = LoginEvent.Error
            } catch (e: IllegalArgumentException) {
                error = "Thông tin không hợp lệ"
                errorDescription =
                    e.localizedMessage ?: "Vui lòng kiểm tra lại thông tin đăng nhập."
                _uiState.value = LoginEvent.Error
            } catch (e: Exception) {

                error = "Lỗi không xác định"
                errorDescription = e.localizedMessage ?: "Vui lòng thử lại."
                _uiState.value = LoginEvent.Error
            }


        }
    }


    sealed class LoginNavigationEvent {
        data object NavigateSignUp : LoginNavigationEvent()
        data object NavigateToAdmin : LoginNavigationEvent()
        data object NavigateToStaff : LoginNavigationEvent()
        data object NavigateToCustomer : LoginNavigationEvent()
        data object NavigateForgot : LoginNavigationEvent()
    }

    sealed class LoginEvent {
        data object Nothing : LoginEvent()
        data object Success : LoginEvent()
        data object Error : LoginEvent()
        data object Loading : LoginEvent()
    }
}
