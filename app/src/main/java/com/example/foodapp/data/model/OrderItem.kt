package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: String,
    val menuItemId: String,
    val orderId: String,
    val quantity: Int,
    val menuItemName: String? = null,
)