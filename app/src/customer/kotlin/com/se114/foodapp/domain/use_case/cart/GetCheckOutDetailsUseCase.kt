package com.se114.foodapp.domain.use_case.cart

import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCheckOutDetailsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(): Flow<CheckoutDetails> {
        try {
           return cartRepository.getCheckoutDetails()
        } catch (e: Exception) {
            throw e
        }
    }
}