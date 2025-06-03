package com.se114.foodapp.domain.use_case.ingredient

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Unit
import com.se114.foodapp.domain.repository.IngredientRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateUnitUseCase @Inject constructor(
    private val ingredientRepository: IngredientRepository
) {
    operator fun invoke(id: Long, name: String) = flow<ApiResponse<Unit>> {

        try {
            ingredientRepository.updateUnit(id= id, name = name).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))

        }
    }.flowOn(Dispatchers.IO)
}