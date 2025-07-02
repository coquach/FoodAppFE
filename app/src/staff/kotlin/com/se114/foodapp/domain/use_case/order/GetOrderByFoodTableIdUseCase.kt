package com.se114.foodapp.domain.use_case.order

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Order
import com.example.foodapp.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetOrderByFoodTableIdUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(tableId: Int) = flow<ApiResponse<Order>> {
        try {
            orderRepository.getOrdersByFoodTableId(tableId).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã xảy ra lỗi trong quá trình tải đơn hàng của bàn", 999))
        }
    }.flowOn(Dispatchers.IO)
}