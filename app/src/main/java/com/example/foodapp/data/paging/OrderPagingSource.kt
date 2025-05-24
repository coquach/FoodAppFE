package com.example.foodapp.data.paging

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.remote.main_api.OrderApi

import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

import javax.inject.Inject


class OrderPagingSource @Inject constructor(
    private val orderApi: OrderApi,
    private val filter: OrderFilter,
    private val customerId: String?=null
) : ApiPagingSource<Order>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Order>>> {
        return apiRequestFlow {
            if (customerId != null) {
                orderApi.getOrdersByCustomerId(
                    page = page,
                    size = size,
                    customerId = customerId)
            } else {
                orderApi.getOrders(
                    page = page,
                    size = size,
                    status = filter.status,
                    paymentMethod = filter.paymentMethod,
                    staffId = filter.staffId,
                    startDate = StringUtils.formatLocalDate(filter.startDate),
                    endDate = StringUtils.formatLocalDate(filter.endDate)
                )
            }

        }
    }
}
