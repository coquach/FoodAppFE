package com.example.foodapp.data.dto.request

data class AddToCartRequest(
    val menuItemId: String,
    val quantity: Int
)
