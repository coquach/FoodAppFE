package com.example.foodapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.response.PageResponse

import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.remote.AiApi
import com.example.foodapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

class ChatBoxPagingSource @Inject constructor(
    private val aiApi: AiApi,
) : ApiPagingSource<ChatMessage>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<ChatMessage>>> {
        return apiRequestFlow {
            aiApi.getChatMessage(page = page, size= size)
        }
    }
}