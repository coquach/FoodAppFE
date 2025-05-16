package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.ChatMessageRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.ChatMessage

import com.example.foodapp.data.paging.ChatBoxPagingSource

import com.example.foodapp.data.remote.AiApi

import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatBoxRepository @Inject constructor(
    private val aiApi: AiApi,
){   fun getAllMessageChat(): Flow<PagingData<ChatMessage>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                enablePlaceholders = true),
            pagingSourceFactory = {
                ChatBoxPagingSource(aiApi = aiApi)
            }
        ).flow
    }
    suspend fun sendMessage(message: String) : ApiResponse<ChatMessage> {
        return safeApiCall { aiApi.chatWithMistralAi(ChatMessageRequest(message)) }
    }
}