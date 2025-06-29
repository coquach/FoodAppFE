package com.example.foodapp.data.model

import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.data.model.enums.ServingType
import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

@Serializable
data class Order(
    val id: Long,
    val tableNumber: Int? = null,
    val voucherDiscount: Double? = null,
    val createdBy: String?=null,
    @Serializable(with = BigDecimalSerializer::class)
    val totalPrice: BigDecimal = BigDecimal.ZERO,
    val status: String= OrderStatus.PENDING.name,
    val method: String = PaymentMethod.CASH.name,
    val type: String = ServingType.ONLINE.name,

    @Serializable(with = LocalDateTimeSerializer::class)
    val startedAt: LocalDateTime? = null,

    @Serializable(with = LocalDateTimeSerializer::class)
    val paymentAt: LocalDateTime?=null,

    val address: Address?= null,
    val sellerId: String? = null,

    val note: String? = null,
    val phone: String? = null,
    val orderItems: List<OrderItem> = emptyList()
)

data class CheckoutUiModel(
    val id: Long? = null,
    val foodTableNumber: Int? = null,
    val voucher: Voucher? = null,
    val method: String,
    val type: String,
    val note: String?=null,
    val phone: String? = null,
    val status: String = OrderStatus.PENDING.name,
    val address: Address? = null,
)
fun Order.toCheckoutUiModel() = CheckoutUiModel(
    id = id,
    foodTableNumber = tableNumber,
    voucher = null,
    method = method,
    type = type,
    note = note,
)