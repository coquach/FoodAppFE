package com.example.foodapp.domain.use_case.order

import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(filter: OrderFilter) = orderRepository.getOrders(filter)

}