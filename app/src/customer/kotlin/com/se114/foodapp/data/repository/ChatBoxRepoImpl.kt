package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.request.ChatMessageRequest
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.data.paging.ChatBoxPagingSource
import com.example.foodapp.data.remote.AiApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.domain.repository.ChatBoxRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatBoxRepoImpl @Inject constructor(
    private val aiApi: AiApi
) : ChatBoxRepository {
    override fun getAllMessageChat(): Flow<PagingData<ChatMessage>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                enablePlaceholders = true),
            pagingSourceFactory = {
                ChatBoxPagingSource(aiApi = aiApi)
            }
        ).flow
    }

    override fun sendMessage(message: String): Flow<ApiResponse<ChatMessage>> {
        return apiRequestFlow {
            aiApi.chatWithMistralAi(ChatMessageRequest(message))
        }
    }
}