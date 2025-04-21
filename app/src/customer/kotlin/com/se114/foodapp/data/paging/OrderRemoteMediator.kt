package com.se114.foodapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.foodapp.data.entities.OrderEntity
import com.example.foodapp.data.entities.OrderRemoteKeys
import com.example.foodapp.data.local.dao.OrderRemoteKeysDao
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.remote.FoodApi
import com.se114.foodapp.data.local.CustomerDatabase
import com.se114.foodapp.utils.Constants.ITEMS_PER_PAGE

@OptIn(ExperimentalPagingApi::class)
class OrderRemoteMediator(
    private val foodApi: FoodApi,
    private val customerDatabase: CustomerDatabase
) : RemoteMediator<Int, Staff>() {

    private val orderDao = customerDatabase.orderDao()
    private val orderRemoteKeysDao = customerDatabase.orderRemoteKesDao()

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

            val response = foodApi.getStaffs(page = currentPage, size = ITEMS_PER_PAGE)
            val items = response.content
            val endOfPaginationReached = items.isEmpty()

            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            customerDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    orderDao.deleteAllOrders()
                    orderRemoteKeysDao.deleteAllRemoteKeys()
                }
                val keys = items.map { order ->
                    OrderRemoteKeys(
                        id = order.id,
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
        state: PagingState<Int, Staff>
    ): StaffRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                staffRemoteKeysDao.getRemoteKeys(id = id.toString())
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Staff>
    ): StaffRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { staff ->
                staffRemoteKeysDao.getRemoteKeys(id = staff.id.toString())
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Staff>
    ): StaffRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { staff ->
                staffRemoteKeysDao.getRemoteKeys(id = staff.id.toString())
            }
    }
}