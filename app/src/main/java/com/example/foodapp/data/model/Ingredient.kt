package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val id: Long,
    val name: String,
    val unitId: Long,
)
