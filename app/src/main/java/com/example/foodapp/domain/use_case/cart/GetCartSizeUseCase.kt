package com.example.foodapp.domain.use_case.cart

import com.se114.foodapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartSizeUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<Int> {
        try {
            return cartRepository.getCartSize()
        }catch (e: Exception){
            throw e
        }

    }

}