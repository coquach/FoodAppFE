package com.example.foodapp.domain.use_case.auth

import com.example.foodapp.domain.repository.AccountRepository
import javax.inject.Inject

class GetCustomerIdUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): String {
        try {
            return accountRepository.currentUserId?: throw Exception("Tài khoản chưa đăng nhập")
        }catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }

}