package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class Inventory(
    val id: Long,
    val ingredientName: String,
    val unit: String,

    @Serializable(with = LocalDateSerializer::class)
    val expiryDate: LocalDate?= null,  // Ngày hết hạn

    @Serializable(with = LocalDateSerializer::class)
    val productionDate: LocalDate?= null,
    @Serializable(with = BigDecimalSerializer::class)
    val cost: BigDecimal,

    @Serializable(with = BigDecimalSerializer::class)
    val quantityRemaining: BigDecimal,
)
