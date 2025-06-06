package com.se114.foodapp.domain.use_case.cart

import com.se114.foodapp.domain.repository.CartRepository
import com.example.foodapp.data.model.CheckoutDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetCheckOutDetailsUseCase @Inject constructor(
    private val cartRepository: CartRepository,
) {
    operator fun invoke(): Flow<CheckoutDetails> = flow {

        emitAll(cartRepository.getCheckoutDetails().flowOn(Dispatchers.IO))
    }.catch { e ->
        throw e
    }.flowOn(Dispatchers.IO)
}
