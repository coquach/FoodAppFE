package com.example.foodapp.domain.use_case.order

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.remote.main_api.OrderApi
import com.example.foodapp.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(request: OrderRequest) = flow<ApiResponse<Order>> {
        try {
             orderRepository.createOrder(request).collect {
                 emit(it)
             }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi đặt hàng", 999))
        }
    }.flowOn(Dispatchers.IO)
}