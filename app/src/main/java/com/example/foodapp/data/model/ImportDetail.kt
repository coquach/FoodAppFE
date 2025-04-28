package com.example.foodapp.data.model


import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class ImportDetail(
    val id: Long,
    val anImport: Import,
    val ingredient: Ingredient,

    @Serializable(with = LocalDateTimeSerializer::class)
    val expiryDate: LocalDateTime,

    @Serializable(with = LocalDateTimeSerializer::class)
    val productionDate: LocalDateTime,

    @Serializable(with = BigDecimalSerializer::class)
    val quantity: BigDecimal,

    @Serializable(with = BigDecimalSerializer::class)
    val cost: BigDecimal,

)