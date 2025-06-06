package com.se114.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatBoxRepository {
    fun getAllMessageChat(): Flow<PagingData<ChatMessage>>
    fun sendMessage(message: String): Flow<ApiResponse<ChatMessage>>
}