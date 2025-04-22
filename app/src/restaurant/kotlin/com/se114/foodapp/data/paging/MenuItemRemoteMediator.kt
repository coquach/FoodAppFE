package com.se114.foodapp.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.entities.MenuItemRemoteKeys
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.mapper.OrderMapper.toEntity
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.example.foodapp.utils.buil_sql.BuildMenuItemQuery

import com.se114.foodapp.data.local.AdminDatabase


@OptIn(ExperimentalPagingApi::class)
class MenuItemRemoteMediator(
    private val foodApi: FoodApi,
    private val adminDatabase: AdminDatabase,
    private val filter: MenuItemFilter,
) : RemoteMediator<Int, MenuItem>() {

    private val menuItemDao = adminDatabase.menuItemDao()
    private val menuItemRemoteKeysDao = adminDatabase.menuItemRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MenuItem>
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

            val response = foodApi.getMenuItems(page = currentPage, size = ITEMS_PER_PAGE, isAvailable = filter.isAvailable)
            val items = response.content.map { item -> item.copy(isAvailable = filter.isAvailable)}
            val endOfPaginationReached = items.isEmpty() || items.size < ITEMS_PER_PAGE

            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            adminDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    val deleteQuery = BuildMenuItemQuery.buildMenuItemQuery(filter, true)
                    val deletedRows = menuItemDao.deleteMenuItemsByFilter(deleteQuery)
                    Log.d("Api get menuItems", "Deleted $deletedRows rows from database")
                    menuItemRemoteKeysDao.deleteAllRemoteKeys()
                }
                val keys = items.map { menuItem ->
                    MenuItemRemoteKeys(
                        id = menuItem.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                Log.d("Api get menuItems", "Inserting ${keys.size} remote keys and ${items.size} menuItems to Room")
                menuItemRemoteKeysDao.addAllRemoteKeys(remoteKeys = keys)
                menuItemDao.addMenuItems(menuItems = items)
                Log.d("Api get menuItems", "Finished insert menu items.")
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MenuItem>
    ): MenuItemRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                menuItemRemoteKeysDao.getRemoteKeys(id = id.toString())
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MenuItem>
    ): MenuItemRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { menuItem ->
                menuItemRemoteKeysDao.getRemoteKeys(id = menuItem.id.toString())
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MenuItem>
    ): MenuItemRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { menuItem ->
                menuItemRemoteKeysDao.getRemoteKeys(id = menuItem.id.toString())
            }
    }

}