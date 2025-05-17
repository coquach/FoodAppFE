package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class Food(
    val id: Long,
    val description: String,
    val images: List<ImageInfo>?,
    val name: String,

    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val defaultQuantity: Int,
    val remainingQuantity: Int,
    val active: Boolean,
    val totalRating: Double,
    val totalFeedback: Int,
    val totalLikes: Int,
    val liked: Boolean,
)