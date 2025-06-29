package com.se114.foodapp.domain.use_case.food_table

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.domain.repository.FoodTableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateOrderForTableUseCase @Inject constructor(
    private val foodTableRepository: FoodTableRepository,
) {
    operator fun invoke(id: Int) = flow<ApiResponse<FoodTable>> {
        try {
            foodTableRepository.createOrderForTable(id).collect {
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã xảy ra lỗi trong quá trình tạo đơn hàng cho bàn", 999))
        }
    }.flowOn(Dispatchers.IO)
}