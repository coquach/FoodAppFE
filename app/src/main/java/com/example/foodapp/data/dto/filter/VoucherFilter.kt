package com.example.foodapp.data.dto.filter

import java.time.LocalDate

data class VoucherFilter(
    val order: String = "desc",
    val sortBy: String = "id",
    val minQuantity: Int? = null,
    val maxQuantity: Int? = null,
    val type: String?=null,
    val startDate: LocalDate?=null,
    val endDate: LocalDate?=null,
    val forceRefresh: String?=null
)
