package com.example.foodapp.domain.use_case.auth

import com.example.foodapp.domain.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {
    operator fun invoke() = flow<FirebaseResult<Unit>> {
        emit(FirebaseResult.Loading)
        try {
            accountRepository.signOut()
            emit(FirebaseResult.Success(Unit))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FirebaseResult.Failure("Lỗi không xác định"))

        }
    }.flowOn(Dispatchers.IO)

}