package com.example.foodapp.data.model

import java.math.BigDecimal

data class CheckoutDetails(
    val deliveryFee: BigDecimal,
    val subTotal: BigDecimal,
    val tax: BigDecimal,
    val totalAmount: BigDecimal
)
