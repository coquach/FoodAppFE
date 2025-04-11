package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FoodItem(
    val createdAt: String? = null,
    val description: String,
    val id: String? = null,
    val imageUrl: String? = null,
    val name: String,
    val price: Float
)