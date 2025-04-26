package com.se114.foodapp.data.repository

import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.se114.foodapp.data.local.StaffDatabase

import com.se114.foodapp.mapper.CartMapper.toCartItem
import com.se114.foodapp.mapper.CartMapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CartRepository @Inject constructor(
    private val staffDatabase: StaffDatabase
) {
    private val cartDao = staffDatabase.cartDao()
    val cartItemsFlow: Flow<List<CartItem>> = cartDao.getCartItems()
        .map { list -> list.map { it.toCartItem() } }

    suspend fun saveCartItems(cartItems: List<CartItem>) {
        cartDao.insertCartItems(cartItems.map { it.toEntity() })
    }

    suspend fun clearCartItems(cartItemsToRemove: List<CartItem>) {
        cartDao.deleteItems(cartItemsToRemove.map { it.toEntity() })
    }

    fun getCartSize(): Flow<Int> {
        return cartItemsFlow.map { it.size }
    }

    suspend fun updateItemQuantity(id: Long, quantity: Int) {
        cartDao.updateQuantity(id, quantity)
    }

    suspend fun clearAll() {
        cartDao.clearAll()
    }
    val checkoutDetailsFlow: Flow<CheckoutDetails> = cartItemsFlow
        .map { cartItems ->
            val subTotal = cartItems.fold(BigDecimal.ZERO) { acc, item ->
                acc + (item.price.multiply(BigDecimal(item.quantity)))
            }

            val taxRate = BigDecimal("0.10")
            val tax = subTotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP)

            val deliveryFee = if (subTotal < BigDecimal(300_000)) {
                BigDecimal(15_000)
            } else {
                BigDecimal.ZERO
            }

            val totalAmount = subTotal + tax + deliveryFee

            CheckoutDetails(
                deliveryFee = deliveryFee,
                subTotal = subTotal,
                tax = tax,
                totalAmount = totalAmount
            )
        }


}
