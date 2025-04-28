package com.example.foodapp.data.dto.request

import java.math.BigDecimal

data class ExportDetailRequest(
    val id: Long,
    val inventoryId: Long,
    val quantity: BigDecimal
)
