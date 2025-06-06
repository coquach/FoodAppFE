package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class ExportDetail(
    val id: Long,
    val inventoryId: Long,
    val ingredientName: String,
    @Serializable(with = LocalDateSerializer::class)
    val expiryDate: LocalDate,
    @Serializable(with = BigDecimalSerializer::class)
    val cost: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val quantity: BigDecimal
)
