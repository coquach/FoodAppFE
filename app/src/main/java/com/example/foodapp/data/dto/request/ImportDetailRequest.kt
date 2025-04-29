package com.example.foodapp.data.dto.request

import java.math.BigDecimal

data class ImportDetailRequest(
    val id: Long?=null,
    val ingredientId: Long?=null,
    val expiryDate: String,
    val productionDate: String,
    val quantity: BigDecimal,
    val cost: BigDecimal
)
