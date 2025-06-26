package com.se114.foodapp.domain.use_case.ai

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.IntentTypeRequest
import com.example.foodapp.data.model.ChatKnowledgeEntry
import com.example.foodapp.data.model.IntentType
import com.example.foodapp.domain.repository.AIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateIntentTypeUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    operator fun invoke(request : IntentTypeRequest) = flow<ApiResponse<IntentType>> {
        try {
            aiRepository.createIntentType(request).collect {
                emit(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã có lỗi xảy ra trong quá trình tạo dữ liệu", 999))
        }
    }.flowOn(Dispatchers.IO)
}