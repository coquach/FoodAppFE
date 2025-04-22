package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.LocalDateSerializer
import com.example.foodapp.utils.json_format.LocalTimeSerializer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class Order(
    val id: Long,
    val customerId: String? = null,
    val tableNumber: Int? = null,
    val voucher: Double? = null,
    val status: String,
    val paymentMethod: String,
    @Serializable(with = LocalDateSerializer::class)
    val orderDate: LocalDate,

    @Serializable(with = LocalTimeSerializer::class)
    val createAt: LocalTime,

    @Serializable(with = LocalTimeSerializer::class)
    val paymentAt: LocalTime,

    val note: String? = null,
    val address: String? = null,
    val servingType: String,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,
    val orderItems: List<OrderItem> = emptyList()
)