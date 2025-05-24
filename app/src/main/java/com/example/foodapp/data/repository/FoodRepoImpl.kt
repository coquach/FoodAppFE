package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.FoodFilter


import com.example.foodapp.data.model.Food
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE

import com.example.foodapp.data.paging.FoodPagingSource
import com.example.foodapp.data.remote.main_api.FoodApi
import com.example.foodapp.domain.repository.FoodRepository


import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class FoodRepoImpl @Inject constructor(
    private val foodApi: FoodApi,
) : FoodRepository {
    override fun getFoodsByMenuId(menuId: Long): Flow<PagingData<Food>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FoodPagingSource(foodApi = foodApi, menuId = menuId)
            }
        ).flow
    }

    override fun addFood(
        menuId: Long,
        request: Map<String, @JvmSuppressWildcards RequestBody>,
        images: List<MultipartBody.Part>?,
    ): Flow<ApiResponse<Food>> {
        return apiRequestFlow {
            foodApi.createFood(
                menuId = menuId,
                request = request,
                images = images
            )
        }
    }

    override fun updateFood(
        foodId: Long,
        menuId: Long,
        request: Map<String, @JvmSuppressWildcards RequestBody>,
        images: List<MultipartBody.Part>?,
    ): Flow<ApiResponse<Food>> {
        return apiRequestFlow {
            foodApi.updateFood(
                menuId = menuId,
                foodId = foodId,
                request = request,
                images = images
            )
        }
    }

    override fun getFavoriteFoods(): Flow<PagingData<Food>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FoodPagingSource(foodApi = foodApi)
            }
        ).flow
    }

    override  fun toggleLike(foodId: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            foodApi.toggleLike(foodId)
        }
    }

    override fun toggleStatus(foodId: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            foodApi.toggleStatus(foodId)
        }
    }


}


