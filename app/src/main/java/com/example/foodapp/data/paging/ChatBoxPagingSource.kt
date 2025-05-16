package com.example.foodapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.remote.AiApi
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants
import java.io.IOException

class ChatBoxPagingSource(
    private val aiApi: AiApi,
) : PagingSource<Int, ChatMessage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatMessage> {
        val currentPage = params.key ?: 0
        return try {
            val response = safeApiCall {

                    aiApi.getChatMessage(
                        page = currentPage,
                        size = Constants.ITEMS_PER_PAGE,
                    )
                }


            when (response) {
                is ApiResponse.Success -> {
                    val body = response.body
                    val endOfPaginationReached =
                        body.content.isEmpty() || body.content.size < Constants.ITEMS_PER_PAGE
                    if (body.content.isNotEmpty()) {
                        LoadResult.Page(
                            data = body.content,
                            prevKey = if (currentPage == 0) null else currentPage - 1,
                            nextKey = if (endOfPaginationReached) null else currentPage + 1
                        )
                    } else {
                        LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                    }
                }

                else -> {
                    LoadResult.Error(Exception("Response body Invalid"))
                }
            }


        } catch (ex: Exception) {
            LoadResult.Error(ex)
        } catch (ex: IOException) {
            LoadResult.Error(ex)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, ChatMessage>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}