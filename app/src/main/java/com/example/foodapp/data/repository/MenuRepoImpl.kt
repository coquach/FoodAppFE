package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.request.MenuRequest
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.paging.FoodPagingSource
import com.example.foodapp.data.paging.MenuPagingSource
import com.example.foodapp.data.remote.main_api.FoodApi
import com.example.foodapp.domain.repository.MenuRepository
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MenuRepoImpl @Inject constructor(
    private val foodApi: FoodApi
): MenuRepository {
    override fun getMenu(): Flow<PagingData<Menu>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                MenuPagingSource(foodApi = foodApi)
            }
        ).flow
    }

    override fun addMenu(request: MenuRequest): Flow<ApiResponse<Menu>> {
        return apiRequestFlow {
            foodApi.createMenu(request)
        }
    }

    override fun updateMenu(
        menuId: Long,
        request: MenuRequest,
    ): Flow<ApiResponse<Menu>> {
       return apiRequestFlow {
           foodApi.updateMenu(menuId, request)
       }
    }

    override fun updateMenuStatus(
        menuId: Long,
        status: Boolean,
    ): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            foodApi.updateMenuStatus(menuId, mapOf("active" to status))
        }
    }
}