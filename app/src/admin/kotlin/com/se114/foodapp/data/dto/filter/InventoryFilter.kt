package com.se114.foodapp.data.dto.filter

import java.time.LocalDate

data class InventoryFilter(
    val ingredientId: Long? = null,
    val expiryDate: String? = null,
    val isOutOfStock: Boolean?= null
)
