package com.example.foodapp.data.paging

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.remote.main_api.FoodApi
import kotlinx.coroutines.flow.Flow

class MenuPagingSource(
    private val foodApi: FoodApi,
) : ApiPagingSource<Menu>() {

    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Menu>>> {
        return apiRequestFlow {
            foodApi.getMenus(page = page, size = size)
        }
    }
}