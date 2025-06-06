package com.se114.foodapp.data.model

import java.math.BigDecimal
import java.time.LocalDate

data class MenuReport (
    val id: Integer,
    val reportDate: LocalDate,
    val menuName: String,
    val purchaseCount: Int
)