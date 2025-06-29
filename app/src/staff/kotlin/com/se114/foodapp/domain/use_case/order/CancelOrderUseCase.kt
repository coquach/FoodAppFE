package com.se114.foodapp.domain.use_case.order

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CancelOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(orderId: Long) = flow<ApiResponse<Unit>> {
        try {
            orderRepository.cancelOrder(orderId).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã xảy ra lỗi trong quá trình hủy đơn hàng", 999))
        }
    }.flowOn(Dispatchers.IO)
}