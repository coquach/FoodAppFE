package com.example.foodapp.data.paging

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.FoodTableFilter
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.data.remote.main_api.FoodTableApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodTablePagingSource @Inject constructor(
    private val foodTableApi: FoodTableApi,
    private val filter: FoodTableFilter
) : ApiPagingSource<FoodTable>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<FoodTable>>> {
        return apiRequestFlow {
            foodTableApi.getFoodTables(
                page = page,
                size = size,
                sortBy = filter.sortBy,
                order = filter.order,
                active = filter.active,
                status = filter.status,

            )
        }
    }
}