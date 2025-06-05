package com.example.foodapp.data.dto.filter

data class InventoryFilter(
    val ingredientId: Long? = null,
    val expiryDate: String? = null,
    val isOutOfStock: Boolean?= null
)