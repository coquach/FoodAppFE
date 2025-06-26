package com.example.foodapp.domain.use_case.auth

import android.util.Log
import com.example.foodapp.BuildConfig
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.notification.FoodAppNotificationManager
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginByEmailUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val foodAppNotificationManager: FoodAppNotificationManager
) {
    operator fun invoke(email: String, password: String) = flow<FirebaseResult<Unit>> {
        emit(FirebaseResult.Loading)
        try {
            accountRepository.signInWithEmail(email, password)
            val role = accountRepository.getUserRole() ?: "customer"
            val currentFlavor = BuildConfig.FLAVOR
            if (role != currentFlavor) {

                emit(FirebaseResult.Failure("Bạn đang dùng app dành cho '$currentFlavor' nhưng tài khoản này là '$role'."))
                return@flow
            }
            Log.d("LoginByEmailUseCase", "Login successful with role: $role")
            foodAppNotificationManager.getAndStoreToken()
            Log.d("LoginByEmailUseCase", "Token stored successfully")
            emit(FirebaseResult.Success(Unit))
        } catch (e: FirebaseAuthInvalidUserException) {
           emit(FirebaseResult.Failure("Tài khoản không tồn tại"))

        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(FirebaseResult.Failure( "Tài khoản không đúng. Vui lòng thử lại."))

        } catch (e: FirebaseAuthException) {
            emit(FirebaseResult.Failure("Lỗi xác thực. Vui lòng thử lại."))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(FirebaseResult.Failure(e.localizedMessage?: "Lỗi không xác định"))
        }
    }.flowOn(Dispatchers.IO)
}


