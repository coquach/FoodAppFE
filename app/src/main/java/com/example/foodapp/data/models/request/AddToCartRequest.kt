package com.example.foodapp.data.models.request

data class AddToCartRequest(
    val menuItemId: String,
    val quantity: Int
)
