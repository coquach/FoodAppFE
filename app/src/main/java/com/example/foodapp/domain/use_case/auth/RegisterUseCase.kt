package com.example.foodapp.domain.use_case.auth

import com.example.foodapp.domain.repository.AccountRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    val accountRepository: AccountRepository
) {
    operator fun invoke(email: String, password: String) = flow<FirebaseResult<Unit>>{
        emit(FirebaseResult.Loading)
        try {
            accountRepository.createAccountWithEmail(email, password)
            emit(FirebaseResult.Success(Unit))
        }catch (e: IllegalArgumentException){
            e.printStackTrace()
            emit(FirebaseResult.Failure("Thông tin không hợp lệ"))
        }

        catch (e: Exception) {
            e.printStackTrace()
            emit(FirebaseResult.Failure("Lỗi không xác định"))
        }
    }.flowOn(Dispatchers.IO)



}