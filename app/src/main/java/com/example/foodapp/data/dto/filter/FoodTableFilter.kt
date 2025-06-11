package com.example.foodapp.data.dto.filter

data class FoodTableFilter(
    val sortBy: String = "id",
    val order: String = "asc",
    val active: Boolean? = null,
)
