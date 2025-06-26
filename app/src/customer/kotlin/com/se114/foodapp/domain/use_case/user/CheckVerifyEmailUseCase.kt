package com.se114.foodapp.domain.use_case.user

import com.example.foodapp.domain.repository.AccountRepository
import javax.inject.Inject

class CheckVerifyEmailUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Boolean {
        return accountRepository.isEmailVerified()
    }
}