package com.example.foodapp.domain.use_case.order

import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.domain.repository.OrderRepository
import com.example.foodapp.domain.use_case.auth.GetCustomerIdUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrdersByCustomerUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val getCustomerIdUseCase: GetCustomerIdUseCase
){
    operator fun invoke(filter: OrderFilter) : Flow<PagingData<Order>> {
        val customerId = getCustomerIdUseCase.invoke()
        return orderRepository.getOrdersByCustomerId(filter, customerId)
    }

}