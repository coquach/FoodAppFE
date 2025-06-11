package com.example.foodapp.domain.use_case.order

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderStatusRequest
import com.example.foodapp.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateStatusOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(orderId: Long, request: OrderStatusRequest) = flow<ApiResponse<Unit>> {
        try {
             orderRepository.updateOrderStatus(orderId, request).collect {
                 emit(it)
             }
        } catch (e: Exception) {
           e.printStackTrace()
           emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi cập nhật trạng thái đơn hàng", 999))
        }
    }.flowOn(Dispatchers.IO)
}