package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val addedAt: String,
    val id: String,
    val menuItemId: FoodItem,
    val quantity: Int,
    val userId: String
)