package com.example.foodapp.domain.use_case.order

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.remote.main_api.OrderApi
import com.example.foodapp.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
) {
    operator fun invoke(request: OrderRequest) : Flow<ApiResponse<Order>> {
        try {
            return orderRepository.createOrder(request)
        }catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }
}