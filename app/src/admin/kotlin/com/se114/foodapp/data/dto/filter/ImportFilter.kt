package com.se114.foodapp.data.dto.filter

import java.time.LocalDate

data class ImportFilter(
    val order: String = "desc",
    val sortBy: String = "id",
    val  supplierName: String="",
    val startDate: LocalDate?= null,
    val endDate: LocalDate?= null,
    val forceRefresh: String?=null
)