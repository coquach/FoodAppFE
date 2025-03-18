package com.example.foodapp.data.models.response

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val arModelUrl: String? = null,
    val createdAt: String? = null,
    val description: String,
    val id: String? = null,
    val imageUrl: String,
    val name: String,
    val price: Double,
)
data class FoodItemResponse(
    val foodItems: List<FoodItem>
)