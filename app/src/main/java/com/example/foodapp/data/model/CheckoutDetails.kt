package com.example.foodapp.data.model

data class CheckoutDetails(
    val deliveryFee: Float,
    val subTotal: Float,
    val tax: Float,
    val totalAmount: Float
)
