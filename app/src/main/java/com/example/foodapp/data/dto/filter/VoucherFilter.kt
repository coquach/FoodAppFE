package com.example.foodapp.data.dto.filter

data class VoucherFilter(
    val order: String = "desc",
    val sortBy: String = "id",
    val minQuantity: Int? = null,
    val maxQuantity: Int? = null,
    val type: String?=null,
    val startDate: String?=null,
    val endDate: String?=null,
)
