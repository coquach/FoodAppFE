package com.se114.foodapp.data.repository

import com.se114.foodapp.domain.repository.CartRepository
import com.example.foodapp.data.mapper.CartMapper.toCartItem
import com.example.foodapp.data.mapper.CartMapper.toEntity
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails

import com.se114.foodapp.data.local.CustomerDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class CartRepoImpl @Inject constructor(
    private val customerDatabase: CustomerDatabase,
) : CartRepository {
    private val cartDao = customerDatabase.cartDao()

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems()
            .map { list -> list.map { it.toCartItem() } }

    }

    override suspend fun saveCartItems(cartItems: List<CartItem>) {
        cartDao.insertCartItems(cartItems.map { it.toEntity() })
    }

    override suspend fun clearCartItems(cartItemsToRemove: List<CartItem>) {
        cartDao.deleteItems(cartItemsToRemove.map { it.toEntity() })
    }

    override fun getCartSize(): Flow<Int> {
        val cartItemsFlow = getCartItems()
        return cartItemsFlow.map { it.size }
    }

    override suspend fun updateItemQuantity(id: Long, quantity: Int) {
        cartDao.updateQuantity(id, quantity)
    }

    override suspend fun clearAll() {
        cartDao.clearAll()
    }

    override fun getCheckoutDetails(): Flow<CheckoutDetails> {
        val cartItemsFlow = getCartItems()
        return cartItemsFlow
            .map { cartItems ->
                val subTotal = cartItems.fold(BigDecimal.ZERO) { acc, item ->
                    acc + (item.price.multiply(BigDecimal(item.quantity)))
                }

                val totalAmount = subTotal

                CheckoutDetails(
                    totalAmount = totalAmount
                )
            }
    }


}