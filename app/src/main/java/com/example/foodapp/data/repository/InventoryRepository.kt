package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.InventoryFilter

import com.example.foodapp.data.model.Inventory

import com.example.foodapp.data.paging.InventoryPagingSource
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val foodApi: FoodApi,

    ) {

    fun getInventoriesByFilter(filter: InventoryFilter): Flow<PagingData<Inventory>> {

        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true),
            pagingSourceFactory = {
                InventoryPagingSource(foodApi = foodApi, filter = filter)
            }
        ).flow
    }
}