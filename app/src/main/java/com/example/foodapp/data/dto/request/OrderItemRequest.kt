package com.example.foodapp.data.dto.request

import com.google.gson.annotations.SerializedName



data class OrderItemRequest(
    val foodId: Long,
    val quantity: Int,
)