package com.se114.foodapp.data.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.request.IngredientRequest
import com.example.foodapp.data.dto.request.UnitRequest
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.data.model.Unit
import com.example.foodapp.data.remote.main_api.IngredientApi
import com.se114.foodapp.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IngredientRepoImpl @Inject constructor(
    private val ingredientApi: IngredientApi
) : IngredientRepository {
    override fun getActiveUnit(): Flow<ApiResponse<List<Unit>>> {
        return apiRequestFlow {
            ingredientApi.getActiveUnits()
        }
    }

    override fun getHiddenUnit(): Flow<ApiResponse<List<Unit>>> {
        return apiRequestFlow {
            ingredientApi.getHiddenUnits()
        }
    }

    override fun createUnit(name: String): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            ingredientApi.createUnit(UnitRequest(name = name))
        }
    }

    override fun updateUnit(
        id: Long,
        name: String,
    ): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            ingredientApi.updateUnit(id = id, request = UnitRequest(name = name))
        }
    }

    override fun deleteUnit(id: Long): Flow<ApiResponse<kotlin.Unit>> {
        return apiRequestFlow {
            ingredientApi.deleteUnit(id = id)
        }
    }

    override fun recoverUnit(
        id: Long,
        isActive: Boolean,
    ): Flow<ApiResponse<kotlin.Unit>> {
        return apiRequestFlow {
            ingredientApi.recoverUnit(id = id, isActive = isActive)
        }
    }

    override fun getActiveIngredient(): Flow<ApiResponse<List<Ingredient>>> {
        return apiRequestFlow {
            ingredientApi.getActiveIngredients()
        }
    }

    override fun getHiddenIngredient(): Flow<ApiResponse<List<Ingredient>>> {
        return apiRequestFlow {
            ingredientApi.getHiddenIngredients()
        }
    }

    override fun getIngredientById(id: Long): Flow<ApiResponse<Ingredient>> {
        return apiRequestFlow {
            ingredientApi.getIngredientById(id = id)
        }
    }

    override fun createIngredient(request: IngredientRequest): Flow<ApiResponse<Ingredient>> {
        return apiRequestFlow {
            ingredientApi.createIngredient(request = request)
        }
    }

    override fun updateIngredient(
        id: Long,
        request: IngredientRequest,
    ): Flow<ApiResponse<Ingredient>> {
        return apiRequestFlow {
            ingredientApi.updateIngredient(id = id, request = request)
        }
    }

    override fun deleteIngredient(id: Long): Flow<ApiResponse<kotlin.Unit>> {
        return apiRequestFlow {
            ingredientApi.deleteIngredient(id = id)
        }
    }

    override fun setActiveIngredient(
        id: Long,
        isActive: Boolean,
    ): Flow<ApiResponse<kotlin.Unit>> {
        return apiRequestFlow {
            ingredientApi.setActiveIngredient(id = id, isActive = isActive)
        }
    }
}