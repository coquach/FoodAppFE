package com.example.foodapp.data.dto.request

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime

data class OrderRequest(
    @SerializedName("customerId")
    val customerId: String? = null,

    @SerializedName("foodTableId")
    val foodTableId: Long? = null,

    @SerializedName("voucherId")
    val voucherId: Long? = null,

    @SerializedName("staffId")
    val staffId: Long? = null,

    @SerializedName("servingType")
    val servingType: String,

    @SerializedName("paymentMethod")
    val paymentMethod: String,

    @SerializedName("orderDate")
    val orderDate: LocalDate,

    @SerializedName("createAt")
    val createAt: LocalTime,

    @SerializedName("paymentAt")
    val paymentAt: LocalTime,

    @SerializedName("note")
    val note: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("orderItems")
    val orderItems: List<OrderItemRequest>
)