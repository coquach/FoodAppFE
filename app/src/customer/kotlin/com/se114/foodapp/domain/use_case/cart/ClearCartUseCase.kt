package com.se114.foodapp.domain.use_case.cart



import com.example.foodapp.data.model.CartItem
import com.example.foodapp.domain.use_case.safeCall
import com.se114.foodapp.domain.repository.CartRepository

import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(cartItems: List<CartItem>) : Result<Unit> {
        return safeCall {
            cartRepository.clearCartItems(cartItems)
        }
    }

}