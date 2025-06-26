package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.FoodTableFilter
import com.example.foodapp.data.dto.request.FoodTableRequest
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.data.paging.FoodTablePagingSource
import com.example.foodapp.data.remote.main_api.FoodTableApi
import com.example.foodapp.domain.repository.FoodTableRepository
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodTableRepoImpl @Inject constructor(
    private val foodTableApi: FoodTableApi,
) : FoodTableRepository {
    override fun getFoodTables(filter: FoodTableFilter): Flow<PagingData<FoodTable>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FoodTablePagingSource(foodTableApi = foodTableApi, filter = filter)
            }
        ).flow
    }

    override fun createFoodTable(request: FoodTableRequest): Flow<ApiResponse<FoodTable>> {
        return apiRequestFlow {
            foodTableApi.createFoodTable(request)
        }
    }

    override fun updateFoodTable(
        id: Int,
        request: FoodTableRequest,
    ): Flow<ApiResponse<FoodTable>> {
        return apiRequestFlow {
            foodTableApi.updateFoodTable(request, id)
        }
    }

    override fun deleteFoodTable(id: Int): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            foodTableApi.deleteFoodTable(id)
        }
    }

    override fun updateFoodTableStatus(
        id: Int,
    ): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            foodTableApi.updateFoodTableStatus(id)
        }

    }
}