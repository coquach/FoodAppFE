package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class Food(
    val id: Long,
    val description: String,
    val images: List<ImageInfo>?= emptyList<ImageInfo>(),
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
            description = "Món ăn truyền thống siêu ngon",
            images = listOf(ImageInfo("https://fake.image/food1.png")),
            name = "Phở bò tái gầu",
            price = BigDecimal("55000"),
            defaultQuantity = 1,
            remainingQuantity = 20,
            active = true,
            totalRating = 4.5,
            totalFeedback = 234,
            totalLikes = 500,
            liked = false
        )
    }
}