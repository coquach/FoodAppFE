package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import com.example.foodapp.data.model.Staff
import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class Import(
    val id: Long,
    var supplierId: Long,
    val supplierName: String,
    val staffId: Long,
    val staffName: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val importDate: LocalDateTime,
    @Serializable(with = BigDecimalSerializer::class)
    val totalPrice: BigDecimal,
    val importDetails: MutableList<ImportDetail> = mutableListOf()
)
