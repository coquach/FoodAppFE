package com.example.foodapp.data.dto.request

import com.example.foodapp.data.model.enums.OrderStatus

data class OrderStatusRequest(
    val status: String= OrderStatus.PENDING.name,
    val sellerId: String?= null,
    val shipperId: String?= null,
    val customerId: String?= null,
)
