package com.se114.foodapp.data.model

import java.math.BigDecimal
import java.time.LocalDate

data class DailyReport (
    val reportDate: LocalDate,
    val totalSales: BigDecimal,
    val totalOrders: Int,
)