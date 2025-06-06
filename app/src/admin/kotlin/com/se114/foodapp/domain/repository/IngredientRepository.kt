package com.se114.foodapp.domain.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.IngredientRequest
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.data.model.Unit
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    fun getActiveUnit(): Flow<ApiResponse<List<Unit>>>
    fun getHiddenUnit(): Flow<ApiResponse<List<Unit>>>
    fun createUnit(name: String): Flow<ApiResponse<Unit>>
    fun updateUnit(id: Long, name: String): Flow<ApiResponse<Unit>>
    fun deleteUnit(id: Long): Flow<ApiResponse<kotlin.Unit>>
    fun recoverUnit(id: Long, isActive: Boolean): Flow<ApiResponse<kotlin.Unit>>

    fun getActiveIngredient(): Flow<ApiResponse<List<Ingredient>>>
    fun getHiddenIngredient(): Flow<ApiResponse<List<Ingredient>>>
    fun getIngredientById(id: Long): Flow<ApiResponse<Ingredient>>
    fun createIngredient(request: IngredientRequest): Flow<ApiResponse<Ingredient>>
    fun updateIngredient(id: Long, request: IngredientRequest): Flow<ApiResponse<Ingredient>>
    fun deleteIngredient(id: Long): Flow<ApiResponse<kotlin.Unit>>
    fun setActiveIngredient(id: Long, isActive: Boolean): Flow<ApiResponse<kotlin.Unit>>

}