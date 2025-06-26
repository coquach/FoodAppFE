package com.example.foodapp.data.model


import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class CartItem(
    val id: Long,
    val name: String,
    val remainingQuantity: Int,
    val quantity: Int,
    val imageUrl: String?= null,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal
)
