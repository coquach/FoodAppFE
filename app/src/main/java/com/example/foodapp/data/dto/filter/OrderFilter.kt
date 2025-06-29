package com.example.foodapp.data.dto.filter

import com.example.foodapp.utils.StringUtils
import java.time.LocalDate
import java.time.LocalTime

data class OrderFilter(
    val sortBy: String = "id",
    val order: String = "desc",
    val status: String? = null,
    val notStatus: String? = null,
    val customerId: String? = null,
    val sellerId: String? = null,
    val shipperId: String?= null,
    val type: String? = null,
    val paymentMethod: String? = null,
    val foodTableId: Int? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val forceRefresh: String?=null
)