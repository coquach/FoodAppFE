package com.example.foodapp.domain.use_case.auth

import com.example.foodapp.domain.repository.AccountRepository
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SendEmailResetUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke(email: String) = flow<FirebaseResult<Unit>> {
        emit(FirebaseResult.Loading)
        try {
            accountRepository.forgetPassword(email)
            emit(FirebaseResult.Success(Unit))
        }catch (e: FirebaseAuthInvalidUserException) {
            emit(FirebaseResult.Failure("Email chưa được đăng ký."))
        }catch (e: Exception) {
            emit(FirebaseResult.Failure("Lỗi không xác định"))
        }
    }.flowOn(Dispatchers.IO)
}