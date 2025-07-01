package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.dto.request.OrderItemRequest
import com.example.foodapp.data.dto.request.OrderItemsBatchRequest
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.dto.request.OrderStatusRequest
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(filter: OrderFilter): Flow<PagingData<Order>>
    fun createOrder(request: OrderRequest): Flow<ApiResponse<Order>>
    fun updateOrderStatus(orderId: Long, status: OrderStatusRequest): Flow<ApiResponse<Unit>>
    fun checkOutOrder(orderId: Long, request: Map<String, Long?>): Flow<ApiResponse<Unit>>
    fun cancelOrder(orderId: Long): Flow<ApiResponse<Unit>>
    fun getOrdersByFoodTableId(tableId: Int): Flow<ApiResponse<Order>>
    fun upsertOrderItems(orderId: Long, request: OrderItemsBatchRequest): Flow<ApiResponse<Order>>


}