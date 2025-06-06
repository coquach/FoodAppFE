package com.example.foodapp.domain.use_case.order

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Order
import com.example.foodapp.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateStatusOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(orderId: Long, status: String) : Flow<ApiResponse<Order>> {
        try {
            return orderRepository.updateOrderStatus(orderId, status)
        } catch (e: Exception) {
           e.printStackTrace()
            throw e
        }
    }
}