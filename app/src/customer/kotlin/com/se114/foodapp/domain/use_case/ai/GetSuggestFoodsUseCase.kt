package com.se114.foodapp.domain.use_case.ai

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Food
import com.example.foodapp.domain.repository.AIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetSuggestFoodsUseCase  @Inject constructor(
    private val aiRepository: AIRepository
){
    operator fun invoke() = flow<ApiResponse<List<Food>>> {
        try {
            aiRepository.suggestFoodsForCurrentUser().collect {
                emit(it)
            }
        }catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã có lỗi xảy ra khi lấy danh sách gợi ý", 401))
        }
    }.flowOn(Dispatchers.IO)

}