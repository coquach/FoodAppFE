package com.se114.foodapp.domain.use_case.cart


import com.example.foodapp.domain.use_case.safeCall
import com.se114.foodapp.domain.repository.CartRepository
import javax.inject.Inject

class ClearAllCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke() : Result<Unit> {
        return safeCall {
            cartRepository.clearAll()
        }
    }
}