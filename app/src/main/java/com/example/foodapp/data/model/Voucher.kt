package com.example.foodapp.data.model

import com.example.foodapp.data.model.enums.VoucherType
import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Voucher(
    val id: Long,
    val code: String,
    val value: Double,
    @Serializable(with = BigDecimalSerializer::class)
    val minOrderPrice: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val maxValue: BigDecimal,

    val total: Int,
    val type: String,

    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,

    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,

    val expired: Boolean,

    @Serializable(with = LocalDateTimeSerializer::class)
    val useAt: LocalDateTime
)
