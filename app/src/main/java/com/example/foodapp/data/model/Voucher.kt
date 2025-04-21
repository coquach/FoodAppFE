package com.example.foodapp.data.model

import com.example.foodapp.data.model.enums.VoucherType
import com.example.foodapp.utils.json_format.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Voucher(
    val id: Long? = null,
    val code: String?= null,
    val value: Double,
    val minOrderValue: Double? = null,
    val maxOrderValue: Double? = null,
    val total: Int,
    val type: VoucherType,

    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,

    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,

    @SerialName("isActive")
    val isActive: Boolean = true
)
