package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import com.example.foodapp.utils.json_format.LocalTimeSerializer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class Order(
    val id: Long,
    val tableNumber: Int? = null,
    val voucherDiscount: Double? = null,
    val createdBy: String,
    val status: String,
    val method: String,
    val type: String,

    @Serializable(with = LocalDateTimeSerializer::class)
    val startedAt: LocalDateTime,

    @Serializable(with = LocalDateTimeSerializer::class)
    val paymentAt: LocalDateTime,

    val note: String? = null,
    val address: String? = null,
    val orderItems: List<OrderItem> = emptyList()
)

data class CheckoutUiModel(
    val foodTableId: Int? = null,
    val voucher: Voucher? = null,
    val method: String,
    val type: String,
    val note: String = "",
    val address: String? = null,
)