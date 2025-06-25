package com.se114.foodapp.domain.use_case.user

import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SendEmailVerifyUseCase @Inject constructor(
    private val accountRepository: AccountRepository
){
    operator fun invoke() = flow<FirebaseResult<Unit>>{
        try {
            emit(FirebaseResult.Loading)
            accountRepository.sendVerifyEmail()
            emit(FirebaseResult.Success(Unit))
        }catch (e: Exception){
            emit(FirebaseResult.Failure( "Đã xảy ra lỗi trong quá trình gửi email xác thực"))
        }
    }.flowOn(Dispatchers.IO)
}