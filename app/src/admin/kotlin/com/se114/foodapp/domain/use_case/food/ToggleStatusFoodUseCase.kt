package com.se114.foodapp.domain.use_case.food

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ToggleStatusFoodUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    operator fun invoke(foodId: Long) = flow<ApiResponse<Unit>> {

        try {
             foodRepository.toggleStatus(foodId).collect { emit(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))

        }
    }.flowOn(Dispatchers.IO)
}