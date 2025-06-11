package com.example.foodapp.data.dto.filter

data class InventoryFilter(
    val ingredientId: Long? = null,
    val isExpired: Boolean? = null,
    val isOutOfStock: Boolean?= null
)