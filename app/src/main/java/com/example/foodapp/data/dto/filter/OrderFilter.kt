package com.example.foodapp.data.dto.filter

import com.example.foodapp.utils.StringUtils
import java.time.LocalDate
import java.time.LocalTime

data class OrderFilter(
    val status: String? = null,
    val paymentMethod: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val staffId: Long? = null,
    val reloadTrigger: Long = System.currentTimeMillis()
)