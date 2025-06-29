package com.example.foodapp.data.dto.filter

data class FoodTableFilter(
    val sortBy: String = "id",
    val order: String = "asc",
    val tableNumber: Int? = null,
    val active: Boolean= true,
    val status: String?=null,
    val forceRefresh: String? = null,
)
