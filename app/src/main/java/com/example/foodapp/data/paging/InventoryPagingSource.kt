package com.example.foodapp.data.paging

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.remote.main_api.InventoryApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InventoryPagingSource @Inject constructor(
    private val inventoryApi: InventoryApi,
    private val filter: InventoryFilter,
) : ApiPagingSource<Inventory>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Inventory>>> {
        return apiRequestFlow {
            inventoryApi.getInventories(
                page = page,
                size = size,
                ingredientId = filter.ingredientId,
                isExpired = filter.isExpired,
                isOutOfStock = filter.isOutOfStock
            )
        }
    }
}