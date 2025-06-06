package com.se114.foodapp.domain.use_case.food_table

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.FoodTableRequest
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.domain.repository.FoodTableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateFoodTableUseCase @Inject constructor(
    private val foodTableRepository: FoodTableRepository,
){
    operator fun invoke(foodTable: FoodTable) = flow<ApiResponse<FoodTable>> {
        emit(ApiResponse.Loading)
        try {
            val request = FoodTableRequest(
                tableNumber = foodTable.tableNumber,
                seatCapacity = foodTable.seatCapacity
            )
            val id = foodTable.id!!.toInt()
            foodTableRepository.updateFoodTable(id, request).collect {
                emit(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi cập nhật bàn ăn", 999))
        }
    }.flowOn(Dispatchers.IO)
}