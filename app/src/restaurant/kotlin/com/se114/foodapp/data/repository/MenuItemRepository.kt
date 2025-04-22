package com.se114.foodapp.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.entities.OrderEntity
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.example.foodapp.utils.buil_sql.BuildMenuItemQuery
import com.example.foodapp.utils.buil_sql.BuildOrderQuery
import com.se114.foodapp.data.local.AdminDatabase

import com.se114.foodapp.data.paging.MenuItemRemoteMediator

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalPagingApi
@Singleton
class MenuItemRepository @Inject constructor(
    private val foodApi: FoodApi,
    private val adminDatabase: AdminDatabase
) {


    fun getMenuItemsByFilter(filter: MenuItemFilter): Flow<PagingData<MenuItem>> {
        Log.d("MenuRepo", "getMenuItemsByFilter triggered")
        val query = BuildMenuItemQuery.buildMenuItemQuery(filter)
        val pagingSourceFactory = { adminDatabase.menuItemDao().getMenuItemsByFilter(query) }
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            remoteMediator = MenuItemRemoteMediator(
                foodApi = foodApi,
                adminDatabase = adminDatabase,
                filter = filter
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}