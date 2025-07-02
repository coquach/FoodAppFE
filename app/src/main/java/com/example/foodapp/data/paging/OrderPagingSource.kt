package com.example.foodapp.data.paging

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.remote.main_api.OrderApi
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class OrderPagingSource @Inject constructor(
    private val orderApi: OrderApi,
    private val filter: OrderFilter,
) : ApiPagingSource<Order>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Order>>> {
        return apiRequestFlow {

                orderApi.getOrders(
                    page = page,
                    size = size,
                    sortBy = filter.sortBy,
                    order = filter.order,
                    status = filter.status,
                    customerId = filter.customerId,
                    sellerId = filter.sellerId,
                    shipperId = filter.shipperId,
                    type = filter.type,
                    foodTableId = filter.foodTableId,
                    notStatus = filter.notStatus,
                    paymentMethod = filter.paymentMethod,
                    startDate = StringUtils.formatLocalDate(filter.startDate),
                    endDate = StringUtils.formatLocalDate(filter.endDate)
                )


        }
    }
}
