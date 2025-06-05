package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.LocalDateTimeSerializer

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class Import(
    val id: Long?= null,
    var supplierId: Long?=null,
    val supplierName: String = "",
    val staffId: Long?=null,
    val staffName: String="",
    @Serializable(with = LocalDateTimeSerializer::class)
    val importDate: LocalDateTime?=null,
    @Serializable(with = BigDecimalSerializer::class)
    val totalPrice: BigDecimal = BigDecimal.ZERO,
    val importDetails: List<ImportDetail> = emptyList()
)
