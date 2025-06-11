package com.example.foodapp.data.model


import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class ImportDetail(
    val id: Long,
    val ingredient: Ingredient,

    @Serializable(with = LocalDateSerializer::class)
    val expiryDate: LocalDate,

    @Serializable(with = LocalDateSerializer::class)
    val productionDate: LocalDate,

    @Serializable(with = BigDecimalSerializer::class)
    val quantity: BigDecimal,

    @Serializable(with = BigDecimalSerializer::class)
    val cost: BigDecimal,

)