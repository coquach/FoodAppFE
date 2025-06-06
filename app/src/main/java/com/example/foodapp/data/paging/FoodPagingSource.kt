package com.example.foodapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.remote.main_api.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

import java.io.IOException


class FoodPagingSource(
    private val foodApi: FoodApi,
    private val menuId: Long?=null,
) : ApiPagingSource<Food>() {

    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Food>>> {
        return apiRequestFlow {
            if (menuId == null) {
                foodApi.getFavoriteFoods(page = page, size = size)
            } else {
                foodApi.getFoods(page = page, size = size, menuId = menuId)
            }

        }
    }
}