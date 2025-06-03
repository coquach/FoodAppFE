package com.se114.foodapp.domain.use_case.ingredient

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.domain.repository.IngredientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RecoverUnitUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    operator fun invoke(id: Long, isActive: Boolean) = flow<ApiResponse<kotlin.Unit>> {

        try {
            ingredientRepository.recoverUnit(id, isActive).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))

        }
    }.flowOn(Dispatchers.IO)
}