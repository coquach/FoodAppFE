package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.paging.InventoryPagingSource
import com.example.foodapp.data.remote.main_api.InventoryApi
import com.example.foodapp.domain.repository.InventoryRepository
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InventoryRepoImpl @Inject constructor(
    private val inventoryApi: InventoryApi,
) : InventoryRepository {
    override fun getInventoriesByFilter(filter: InventoryFilter): Flow<PagingData<Inventory>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                InventoryPagingSource(inventoryApi = inventoryApi, filter = filter)
            }
        ).flow
    }
}