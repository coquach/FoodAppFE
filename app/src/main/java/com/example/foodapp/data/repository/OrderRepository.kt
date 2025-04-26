package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData

import com.example.foodapp.data.dto.filter.OrderFilter

import com.example.foodapp.data.model.Order
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.example.foodapp.data.paging.OrderPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val foodApi: FoodApi
) {

    fun getOrdersByFilter(filter: OrderFilter): Flow<PagingData<Order>> {

        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                OrderPagingSource(foodApi = foodApi, filter = filter)
            }
        ).flow
    }


}