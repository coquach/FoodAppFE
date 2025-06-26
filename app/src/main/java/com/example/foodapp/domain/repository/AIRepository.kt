package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.ChatKnowledgeEntryRequest
import com.example.foodapp.data.dto.request.IntentTypeRequest
import com.example.foodapp.data.model.ChatKnowledgeEntry
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.IntentType
import kotlinx.coroutines.flow.Flow

interface AIRepository {
    fun getChatKnowledgeEntry(): Flow<ApiResponse<List<ChatKnowledgeEntry>>>
    fun createChatKnowledgeEntry(request: ChatKnowledgeEntryRequest): Flow<ApiResponse<ChatKnowledgeEntry>>
    fun updateChatKnowledgeEntry(id: Long, request: ChatKnowledgeEntryRequest): Flow<ApiResponse<ChatKnowledgeEntry>>
    fun deleteChatKnowledgeEntry(id: Long): Flow<ApiResponse<Unit>>
    fun getAllMessageChat(): Flow<PagingData<ChatMessage>>
    fun sendMessage(message: String): Flow<ApiResponse<ChatMessage>>
    fun getIntentTypes(): Flow<ApiResponse<List<IntentType>>>
    fun createIntentType(request: IntentTypeRequest): Flow<ApiResponse<IntentType>>
    fun updateIntentType(id: Long, request: IntentTypeRequest): Flow<ApiResponse<IntentType>>
    fun deleteIntentType(id: Long): Flow<ApiResponse<Unit>>
    fun suggestFoodsForCurrentUser(): Flow<ApiResponse<List<Food>>>

}