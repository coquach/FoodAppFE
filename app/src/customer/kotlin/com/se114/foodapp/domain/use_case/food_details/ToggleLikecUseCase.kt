package com.se114.foodapp.domain.use_case.food_details

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.FoodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ToggleLikecUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    suspend operator fun invoke(foodId: Long): ApiResponse<Unit> {
        return try {
            foodRepository.toggleLike(foodId).first { it !is ApiResponse.Loading }
        } catch (e: Exception) {
            ApiResponse.Failure(e.message ?: "Có lỗi xảy ra", 999)
        }
    }
}

