package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class FoodItem(
    val createdAt: String? = null,
    val description: String,
    val id: String? = null,
    val imageUrl: String? = null,
    val name: String,

    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
)