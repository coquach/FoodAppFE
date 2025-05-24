package com.example.foodapp.domain.use_case.cart

import com.example.foodapp.data.model.CartItem
import com.se114.foodapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(): Flow<List<CartItem>> {
        try {
            return cartRepository.getCartItems()
        } catch (e: Exception) {
            throw e
        }
    }
}