package com.example.foodapp.data.dto.request

import java.math.BigDecimal
import java.time.LocalDate

data class VoucherRequest(
    val code: String,
    val value: Double,
    val minOrderPrice: BigDecimal,
    val maxValue: BigDecimal,
    val quantity: Int,
    val type: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
)


