package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.FoodTableRequest
import com.example.foodapp.data.model.FoodTable
import kotlinx.coroutines.flow.Flow

interface FoodTableRepository {
    fun getFoodTables(): Flow<PagingData<FoodTable>>
    fun createFoodTable(request: FoodTableRequest): Flow<ApiResponse<FoodTable>>
    fun updateFoodTable(id: Long, request: FoodTableRequest): Flow<ApiResponse<FoodTable>>
    fun deleteFoodTable(id: Long): Flow<ApiResponse<Unit>>
    fun updateFoodTableStatus(id: Long, status: Boolean): Flow<ApiResponse<Unit>>
}