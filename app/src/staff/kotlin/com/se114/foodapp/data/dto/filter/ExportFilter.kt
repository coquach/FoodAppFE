package com.se114.foodapp.data.dto.filter

import java.time.LocalDate

data class ExportFilter(
    val staffId: Long? = null,
    val startDate: String? = null,
    val endDate: String? = null,
)