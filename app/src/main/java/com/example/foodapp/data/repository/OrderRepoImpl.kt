package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow

import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.dto.request.OrderItemRequest
import com.example.foodapp.data.dto.request.OrderItemsBatchRequest
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.dto.request.OrderStatusRequest

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



    override fun createOrder(request: OrderRequest): Flow<ApiResponse<Order>> {
        return apiRequestFlow {
            orderApi.createOrder(request)
        }
    }

    override fun updateOrderStatus(
        orderId: Long,
        status: OrderStatusRequest,
    ): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            orderApi.updateOrderStatus(orderId, status)
        }
    }

    override fun checkOutOrder(
        orderId: Long,
        request: Map<String, Long?>,
    ): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            orderApi.checkOutOrder(orderId, request)
        }
    }

    override fun cancelOrder(orderId: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            orderApi.cancelOrder(orderId)
        }
    }

    override fun getOrdersByFoodTableId(tableId: Int): Flow<ApiResponse<Order>> {
        return apiRequestFlow {
            orderApi.getOrdersByFoodTableId(tableId)
        }
    }

    override fun upsertOrderItems(
        orderId: Long,
        request: OrderItemsBatchRequest,
    ): Flow<ApiResponse<Order>> {
        return apiRequestFlow {
            orderApi.upsertOrderItems(id =orderId, request = request)
        }
    }


}