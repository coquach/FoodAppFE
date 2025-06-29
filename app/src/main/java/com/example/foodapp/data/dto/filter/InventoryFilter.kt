package com.example.foodapp.data.dto.filter

data class InventoryFilter(
    val order: String = "desc",
    val sortBy: String = "id",
    val ingredientName: String = "",
    val isExpired: Boolean? = null,
    val isOutOfStock: Boolean?= null
)