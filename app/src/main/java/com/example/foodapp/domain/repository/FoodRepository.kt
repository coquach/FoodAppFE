package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.FoodFilter
import com.example.foodapp.data.model.Food
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FoodRepository {
    fun getFoodsByMenuId(foodFilter: FoodFilter): Flow<PagingData<Food>>
    fun addFood(request: Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>? = null): Flow<ApiResponse<Food>>
    fun updateFood(foodId: Long, request: Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part?>? = null): Flow<ApiResponse<Food>>
    fun getFavoriteFoods(foodFilter: FoodFilter): Flow<PagingData<Food>>
    fun toggleLike(foodId: Long): Flow<ApiResponse<Boolean>>
    fun toggleStatus(foodId: Long): Flow<ApiResponse<Unit>>
}