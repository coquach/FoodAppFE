package com.example.foodapp.data.dto.request

import com.google.gson.annotations.SerializedName

data class OrderItemRequest(
    val id: Long?,
    val menuItemId: Long,
    val quantity: Int,
    @SerializedName("isDeleted")
    val isDeleted: Boolean
)