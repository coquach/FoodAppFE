package com.se114.foodapp.data.dto.filter

import java.time.LocalDate

data class ImportFilter(
 val staffId: Long?=null,
    val  supplierId: Long?=null,
    val startDate: LocalDate?= null,
    val endDate: LocalDate?= null
)