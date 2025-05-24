package com.example.foodapp.data.dto.request




data class OrderRequest(
    val foodTableId: Int? = null,
    val voucherId: Long? = null,
    val type: String,
    val method: String,
    val startAt: String,
    val paymentAt: String,
    val note: String? = null,
    val address: String? = null,

    val orderItems: List<OrderItemRequest>
){
    companion object {
        fun emptySample(): OrderRequest {
            return OrderRequest(
                foodTableId = null,
                voucherId = null,
                type = "",
                method = "",
                startAt = "",
                paymentAt = "",
                note = null,
                address = null,
                orderItems = emptyList()
            )
        }
    }
}
