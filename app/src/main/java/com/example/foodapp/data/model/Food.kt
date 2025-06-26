package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class Food(
    val id: Long,
    val description: String,
    val images: List<ImageInfo>?= emptyList(),
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
){
    companion object {
        fun sample() = Food(
            id = 1L,
            description = "",
            images = null,
            name = "",
            price = BigDecimal.ZERO,
            defaultQuantity = 1,
            remainingQuantity = 1,
            active = true,
            totalRating = 5.0,
            totalFeedback = 234,
            totalLikes = 500,
            liked = false
        )
    }
}