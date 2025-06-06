package com.se114.foodapp.data.dto.request

import java.math.BigDecimal

data class ExportDetailRequest(
    val id: Long? = null,
    val inventoryId: Long?=null,
    val quantity: BigDecimal = BigDecimal.ONE
)