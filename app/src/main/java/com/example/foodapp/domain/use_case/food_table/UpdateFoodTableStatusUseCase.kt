package com.example.foodapp.domain.use_case.food_table

import com.example.foodapp.data.dto.ApiResponse

import com.example.foodapp.domain.repository.FoodTableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateFoodTableStatusUseCase @Inject constructor(
    private val foodTableRepository: FoodTableRepository
) {
    operator fun invoke(id: Int, status: Boolean) = flow<ApiResponse<Unit>> {
        try {
            foodTableRepository.updateFoodTableStatus(id, status).collect{
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi cập nhật trạng thái bàn ăn", 999))
        }
    }.flowOn(Dispatchers.IO)
}