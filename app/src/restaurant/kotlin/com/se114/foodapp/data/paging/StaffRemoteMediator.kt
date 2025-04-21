package com.se114.foodapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.foodapp.data.remote.FoodApi
import com.se114.foodapp.data.local.AdminDatabase
import com.example.foodapp.data.model.Staff
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE

import com.se114.foodapp.data.model.StaffRemoteKeys


@OptIn(ExperimentalPagingApi::class)
class StaffRemoteMediator(
    private val foodApi: FoodApi,
    private val adminDatabase: AdminDatabase
) : RemoteMediator<Int, Staff>() {

    private val staffDao = adminDatabase.staffDao()
    private val staffRemoteKeysDao = adminDatabase.staffRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Staff>
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

            adminDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    staffDao.deleteAllStaffs()
                    staffRemoteKeysDao.deleteAllRemoteKeys()
                }
                val keys = items.map { staff ->
                    StaffRemoteKeys(
                        id = staff.id.toString(),
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                staffRemoteKeysDao.addAllRemoteKeys(remoteKeys = keys)
                staffDao.addStaffs(staffs = items)
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