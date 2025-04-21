package com.se114.foodapp.data.paging

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.entities.OrderEntity
import com.example.foodapp.data.entities.OrderRemoteKeys
import com.example.foodapp.data.local.dao.OrderRemoteKeysDao
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.mapper.OrderMapper.toEntity
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.example.foodapp.utils.StringUtils
import com.example.foodapp.utils.buil_sql.BuildOrderQuery

import com.se114.foodapp.data.local.StaffDatabase


@OptIn(ExperimentalPagingApi::class)
class OrderRemoteMediator(
    private val foodApi: FoodApi,
    private val staffDatabase: StaffDatabase,
    private val filter: OrderFilter,
) : RemoteMediator<Int, OrderEntity>() {

    private val orderDao = staffDatabase.orderDao()
    private val orderRemoteKeysDao = staffDatabase.orderRemoteKesDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OrderEntity>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 0
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            Log.d("Api get orders", "START CALL")
            val response = foodApi.getOrders(
                page = currentPage, size = ITEMS_PER_PAGE,
                status = filter.status,
                paymentMethod = filter.paymentMethod,
                startDate = StringUtils.formatLocalDate(filter.startDate),
                endDate = StringUtils.formatLocalDate(filter.endDate),
                staffId = filter.staffId
            )
            Log.d("Api get orders", "RESPONSE: ${response.content.size}")
            val items = response.content.map { it.toEntity() }
            val endOfPaginationReached = items.isEmpty()

            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            staffDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    val deleteQuery = BuildOrderQuery.buildOrderQuery(filter, true)
                    val deletedRows = orderDao.deleteOrdersByFilter(deleteQuery)
                    Log.d("Api get orders", "Deleted $deletedRows rows from database")
                    orderRemoteKeysDao.deleteAllRemoteKeys()
                }
                val keys = items.map { order ->
                    OrderRemoteKeys(
                        id = order.id.toString(),
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                orderRemoteKeysDao.addAllRemoteKeys(remoteKeys = keys)
                orderDao.addOrders(orders = items)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, OrderEntity>
    ): OrderRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                orderRemoteKeysDao.getRemoteKeys(id = id.toString())
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, OrderEntity>
    ): OrderRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { staff ->
                orderRemoteKeysDao.getRemoteKeys(id = staff.id.toString())
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, OrderEntity>
    ): OrderRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { staff ->
                orderRemoteKeysDao.getRemoteKeys(id = staff.id.toString())
            }
    }
}