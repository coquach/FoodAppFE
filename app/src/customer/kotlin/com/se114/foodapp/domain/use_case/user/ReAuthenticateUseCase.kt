package com.se114.foodapp.domain.use_case.user

import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ReAuthenticateUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(email: String, password: String) = flow<FirebaseResult<Unit>>{
        try {
            emit(FirebaseResult.Loading)
            accountRepository.reAuthenticateWithEmail(email, password)
            emit(FirebaseResult.Success(Unit))
        }catch (e: Exception){
            e.printStackTrace()
            emit(FirebaseResult.Failure("Đã có lỗi xảy ra trong quá trình xác thực tài khoản"))
        }
    }.flowOn(Dispatchers.IO)
}