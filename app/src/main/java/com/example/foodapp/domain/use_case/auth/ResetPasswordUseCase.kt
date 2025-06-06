package com.example.foodapp.domain.use_case.auth

import com.example.foodapp.domain.repository.AccountRepository
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val accountRepository: AccountRepository
){
    operator fun invoke(oobCode: String, newPassword: String) = flow<FirebaseResult<Unit>> {
        emit(FirebaseResult.Loading)
        try {
            accountRepository.resetPassword(oobCode, newPassword)
            emit(FirebaseResult.Success(Unit))
        }catch (e: FirebaseAuthException) {
            emit(FirebaseResult.Failure(e.message ?: "Đã xảy ra lỗi trong quá trình đặt lại mật khẩu."))

        }
        catch (e: Exception) {
            emit(FirebaseResult.Failure(e.message ?: "Lỗi không xác định"))
        }
    }.flowOn(Dispatchers.IO)
}