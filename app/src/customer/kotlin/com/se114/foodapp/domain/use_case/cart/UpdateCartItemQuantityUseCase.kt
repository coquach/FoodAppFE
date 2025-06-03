package com.se114.foodapp.domain.use_case.cart

import com.example.foodapp.domain.use_case.safeCall
import com.example.foodapp.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    suspend operator fun invoke(id: Long, quantity: Int): Result<Unit> {
        return safeCall {
            cartRepository.updateItemQuantity(id, quantity)
        }
    }
}