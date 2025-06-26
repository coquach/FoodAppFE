package com.se114.foodapp.domain.use_case.ai

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.domain.repository.AIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetMessageChatUseCase @Inject constructor(
    private val aiRepository: AIRepository
) {
    operator fun invoke() = aiRepository.getAllMessageChat()
}