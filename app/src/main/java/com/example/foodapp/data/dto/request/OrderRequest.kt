package com.example.foodapp.data.dto.request

import com.example.foodapp.data.model.Address


data class OrderRequest(
    val foodTableId: Int? = null,
    val voucherId: Long? = null,
    val type: String,
    val method: String,
    val status: String,
    val startedAt: String,
    val note: String? = null,
    val address: Address? = null,
    val phone: String? = null,
    val sellerId: String? = null,
    val shipperId: String? = null,
    val customerId: String? = null,
    val orderItems: List<OrderItemRequest>
){
    companion object {
        fun emptySample(): OrderRequest {
            return OrderRequest(
                foodTableId = null,
                voucherId = null,
                type = "",
                method = "",
                startedAt = "",
                note = null,
                address = null,
                phone = null,
                sellerId = null,
                shipperId = null,
                customerId = null,
                status = "",
                orderItems = emptyList()
            )
        }
    }
}
