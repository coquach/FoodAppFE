package com.example.foodapp.domain.use_case.auth

import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.domain.use_case.safeCall
import javax.inject.Inject

class GetUserRoleUseCase @Inject constructor(
    private val accountRepository: AccountRepository

) {
    suspend operator fun invoke(): Result<String> {
        return safeCall {
           accountRepository.getUserRole()?: throw Exception("Không tìm thấy vai trò")
        }
    }
}