package com.se114.foodapp.domain.use_case.food_details

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ToggleLikecUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
) {
    operator fun invoke(foodId: Long): Flow<ApiResponse<Unit>> {
        try {
            return foodRepository.toggleLike(foodId)
        } catch (e: Exception) {
            throw e
        }

    }
}