package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.request.ChatMessageRequest
import com.example.foodapp.data.dto.request.IntentTypeRequest
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.IntentType
import com.example.foodapp.data.paging.ChatBoxPagingSource
import com.example.foodapp.data.remote.AiApi
import com.example.foodapp.domain.repository.AIRepository
import com.example.foodapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AIRepoImpl @Inject constructor(
    private val aiApi: AiApi
) : AIRepository {
    override fun getAllMessageChat(): Flow<PagingData<ChatMessage>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.ITEMS_PER_PAGE,
                initialLoadSize = Constants.ITEMS_PER_PAGE,
                enablePlaceholders = true
            ),
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

    override fun getIntentTypes(): Flow<ApiResponse<List<IntentType>>> {
        return apiRequestFlow {
            aiApi.getIntentTypes()
        }
    }

    override fun createIntentType(request: IntentTypeRequest): Flow<ApiResponse<IntentType>> {
        return apiRequestFlow {
            aiApi.createIntentType(request)
        }
    }

    override fun updateIntentType(
        id: Long,
        request: IntentTypeRequest,
    ): Flow<ApiResponse<IntentType>> {
        return apiRequestFlow {
            aiApi.updateIntentType(id, request)
        }
    }

    override fun deleteIntentType(id: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            aiApi.deleteIntentType(id)
        }
    }

    override fun suggestFoodsForCurrentUser(): Flow<ApiResponse<List<Food>>> {
       return apiRequestFlow {
           aiApi.suggestFoodsForCurrentUser()
       }
    }
}