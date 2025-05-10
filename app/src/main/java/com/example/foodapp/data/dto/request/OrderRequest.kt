package com.example.foodapp.data.dto.request

import kotlinx.serialization.Serializable


data class OrderRequest(

    val customerId: String? = null,


    val foodTableId: Int? = null,


    val voucherId: Long? = null,




    val type: String,


    val method: String,


    val startAt: String,


    val paymentAt: String,


    val note: String? = null,


    val address: String? = null,


    val orderItems: List<OrderItemRequest>
)