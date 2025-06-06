package com.se114.foodapp.data.model

import java.math.BigDecimal
import java.time.LocalDate


data class MonthlyReport(
    val reportMonth: LocalDate,
    val totalSales: BigDecimal,
    val totalImportCost: BigDecimal,
    val totalSalaries: BigDecimal,
    val netProfit: BigDecimal
)
