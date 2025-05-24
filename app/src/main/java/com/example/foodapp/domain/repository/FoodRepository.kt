package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Food
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FoodRepository {
    fun getFoodsByMenuId(menuId: Long): Flow<PagingData<Food>>
    fun addFood(menuId : Long, request: Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>? = null): Flow<ApiResponse<Food>>
    fun updateFood(foodId: Long, menuId : Long, request: Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>? = null): Flow<ApiResponse<Food>>
    fun getFavoriteFoods(): Flow<PagingData<Food>>
    fun toggleLike(foodId: Long): Flow<ApiResponse<Unit>>
    fun toggleStatus(foodId: Long): Flow<ApiResponse<Unit>>
}