package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow

import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.dto.request.OrderRequest

import com.example.foodapp.data.model.Order
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.example.foodapp.data.paging.OrderPagingSource
import com.example.foodapp.data.remote.main_api.OrderApi
import com.example.foodapp.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class OrderRepoImpl @Inject constructor(
    private val orderApi: OrderApi
) : OrderRepository {
    override fun getOrders(filter: OrderFilter): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                OrderPagingSource(orderApi = orderApi, filter = filter)
            }
        ).flow
    }

    override fun getOrdersByCustomerId(filter: OrderFilter, customerId: String): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                OrderPagingSource(orderApi = orderApi, filter = filter, customerId = customerId)
            }
        ).flow
    }

    override fun createOrder(request: OrderRequest): Flow<ApiResponse<Order>> {
        return apiRequestFlow {
            orderApi.createOrder(request)
        }
    }

    override fun updateOrderStatus(
        orderId: Long,
        status: String,
    ): Flow<ApiResponse<Order>> {
        return apiRequestFlow {
            orderApi.updateOrderStatus(orderId, status)
        }
    }


}