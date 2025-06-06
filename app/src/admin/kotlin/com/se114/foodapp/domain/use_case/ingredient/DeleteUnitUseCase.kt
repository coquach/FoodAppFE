package com.se114.foodapp.domain.use_case.ingredient

import com.example.foodapp.data.dto.ApiResponse

import com.se114.foodapp.domain.repository.IngredientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteUnitUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    operator fun invoke(id: Long) = flow<ApiResponse<Unit>> {

        try {
            ingredientRepository.deleteUnit(id = id).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))

        }
    }.flowOn(Dispatchers.IO)
}