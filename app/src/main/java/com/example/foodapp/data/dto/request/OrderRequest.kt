package com.example.foodapp.data.dto.request

import kotlinx.serialization.Serializable


data class OrderRequest(

    val customerId: String? = null,


    val foodTableId: Long? = null,


    val voucherId: Long? = null,


    val staffId: Long? = null,


    val servingType: String,


    val paymentMethod: String,


    val orderDate: String,


    val createAt: String,


    val paymentAt: String,


    val note: String? = null,


    val address: String? = null,


    val orderItems: List<OrderItemRequest>
)