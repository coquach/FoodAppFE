package com.se114.foodapp.domain.use_case.food_table

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.FoodTableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteFoodTableUseCase @Inject constructor(
    private val foodTableRepository: FoodTableRepository
) {
    operator fun invoke(id: Long) = flow<ApiResponse<Unit>> {
        try {
            foodTableRepository.deleteFoodTable(id).collect {
                emit(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi xóa bàn ăn", 999))

        }
    }.flowOn(Dispatchers.IO)
}