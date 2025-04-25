package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.MenuItemFilter

import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE

import com.se114.foodapp.data.paging.MenuItemPagingSource



import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MenuItemRepository @Inject constructor(
    private val foodApi: FoodApi,

) {

    fun getMenuItemsByFilter(filter: MenuItemFilter): Flow<PagingData<MenuItem>> {

        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = false),
            pagingSourceFactory = {
                MenuItemPagingSource(foodApi = foodApi, filter = filter)
            }
        ).flow
    }
}