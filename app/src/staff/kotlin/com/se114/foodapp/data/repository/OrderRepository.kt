package com.se114.foodapp.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.entities.OrderEntity
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.example.foodapp.utils.buil_sql.BuildOrderQuery
import com.se114.foodapp.data.local.StaffDatabase
import com.se114.foodapp.data.paging.OrderRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalPagingApi
@Singleton
class OrderRepository @Inject constructor(
    private val foodApi: FoodApi,
    private val staffDatabase: StaffDatabase
) {


    fun getOrdersByFilter(filter: OrderFilter): Flow<PagingData<OrderEntity>> {
        val query = BuildOrderQuery.buildOrderQuery(filter)
        val pagingSourceFactory = { staffDatabase.orderDao().getOrdersByFilter(query) }
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            remoteMediator = OrderRemoteMediator(
                foodApi = foodApi,
                staffDatabase = staffDatabase,
                filter = filter
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}