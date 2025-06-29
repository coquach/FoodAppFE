package com.se114.foodapp.domain.use_case.order

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CheckoutOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(orderId: Long, voucherId: Long?) = flow<ApiResponse<Unit>> {
        try {
            orderRepository.checkOutOrder(orderId, mapOf("voucherId" to voucherId)).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã xảy ra lỗi trong quá trình thanh toán đơn hàng", 999))
        }
    }.flowOn(Dispatchers.IO)
}