package com.se114.foodapp.domain.use_case.ai

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.ChatKnowledgeEntryRequest
import com.example.foodapp.data.model.ChatKnowledgeEntry
import com.example.foodapp.domain.repository.AIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateChatKnowUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    operator fun invoke(request: ChatKnowledgeEntryRequest) = flow<ApiResponse<ChatKnowledgeEntry>> {
        try {
            aiRepository.createChatKnowledgeEntry(request).collect {
                emit(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã có lỗi xảy ra trong quá trình tạo dữ liệu", 999))
        }
    }.flowOn(Dispatchers.IO)
}