package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.LocalDateTimeSerializer

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Import(
    val id: Long?= null,
    var supplierId: Long?=null,
    val supplierName: String = "",
    @Serializable(with = LocalDateSerializer::class)
    val importDate: LocalDate?=null,
    @Serializable(with = BigDecimalSerializer::class)
    val totalPrice: BigDecimal = BigDecimal.ZERO,
    val importDetails: List<ImportDetail> = emptyList()
)
