package com.se114.foodapp.domain.use_case.cart

import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.Food
import com.se114.foodapp.domain.repository.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    sealed interface Result {
        data object Loading : Result
        data object ItemUpdated : Result
        data object ItemAdded : Result
        data class Failure(val errorMessage: String) : Result
    }

    operator fun invoke(food: Food, quantity: Int): Flow<Result> = flow {
        emit(Result.Loading)
        delay(1500L)
        val cartItems = cartRepository.getCartItems().first()
        val current = cartItems.toMutableList()
        val index = current.indexOfFirst { it.id == food.id }

        if (index != -1) {
            val updatedItem = current[index].copy(quantity = quantity)
            current[index] = updatedItem
            cartRepository.saveCartItems(current)
            emit(Result.ItemUpdated)
        } else {
            val newItem = CartItem(
                id = food.id,
                name = food.name,
                quantity = quantity,
                price = food.price,
                imageUrl = food.images?.firstOrNull()?.url,
                remainingQuantity = food.remainingQuantity
            )
            current.add(newItem)
            cartRepository.saveCartItems(current)
            emit(Result.ItemAdded)
        }
    }.catch { e ->
        emit(Result.Failure(e.message ?: "Có lỗi xảy ra khi thêm vào giỏ hàng"))
    }.flowOn(Dispatchers.IO)


}