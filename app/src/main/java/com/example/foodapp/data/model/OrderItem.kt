package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class OrderItem(
    val id: Long,
    val menuId: Long,
    val menuItemName: String,
    @Serializable(with = BigDecimalSerializer::class)
    val currentPrice: BigDecimal,
    val quantity: Int,

    @SerialName("isDeleted")
    val isDeleted: Boolean
)