package com.example.foodapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.ChatMessage
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.IOException

abstract class ApiPagingSource<T : Any> : PagingSource<Int, T>() {
    abstract suspend fun fetch(page: Int, size: Int) : Flow<ApiResponse<PageResponse<T>>>

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val currentPage = params.key ?: 0
        val pageSize = ITEMS_PER_PAGE
        return try {
            val response = fetch(currentPage, pageSize).first{it !is ApiResponse.Loading}
            when (response) {
                is ApiResponse.Success -> {

                    val data = response.data
                    val end = data.content.isEmpty() || data.content.size < pageSize
                    if (data.content.isNotEmpty()) {
                        LoadResult.Page(
                            data = data.content,
                            prevKey = if (currentPage == 0) null else currentPage - 1,
                            nextKey = if (end) null else currentPage + 1
                        )
                    } else {
                        LoadResult.Page(
                            data = emptyList(),
                            prevKey = null,
                            nextKey = null
                        )
                    }
                }


                is ApiResponse.Failure -> LoadResult.Error(Exception(response.errorMessage))
                else -> LoadResult.Error(Exception("Unexpected state"))
            }
        }catch (ex: Exception) {
            LoadResult.Error(ex)
        } catch (ex: IOException) {
            LoadResult.Error(ex)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPage = state.closestPageToPosition(anchorPosition)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
    }
}
