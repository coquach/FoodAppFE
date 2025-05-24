package com.se114.foodapp.domain.use_case.food_details

import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.Food
import com.se114.foodapp.domain.repository.CartRepository

import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    sealed class Result {
        data object ItemUpdated : Result()
        data object ItemAdded : Result()
    }

    suspend operator fun invoke(food: Food, quantity: Int): Result {
        return try {
            val cartItems = cartRepository.getCartItems().first()
            val current = cartItems.toMutableList()
            val index = current.indexOfFirst { it.id == food.id }

            if (index != -1) {
                val updatedItem = current[index].copy(quantity = quantity)
                current[index] = updatedItem
                cartRepository.saveCartItems(current)
                Result.ItemUpdated
            } else {
                val newItem = CartItem(
                    id = food.id,
                    name = food.name,
                    quantity = quantity,
                    price = food.price,
                    imageUrl = food.images?.first()?.url
                )
                current.add(newItem)
                cartRepository.saveCartItems(current)
                Result.ItemAdded
            }

        } catch (e: Exception) {
            throw Exception(e.message?: "Có lỗi xảy ra khi thêm vào giỏ hàng")
        }
    }

}