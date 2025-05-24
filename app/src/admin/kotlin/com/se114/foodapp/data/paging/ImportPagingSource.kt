package com.se114.foodapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.FoodFilter
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Food
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.data.dto.filter.ImportFilter
import java.io.IOException

class ImportPagingSource(
    private val foodApi: FoodApi,
    private val filter: ImportFilter,
) : PagingSource<Int, Import>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Import> {
        val currentPage = params.key ?: 0
        return try {
            val response = safeApiCall {
                foodApi.getImports(
                    page = currentPage,
                    size = ITEMS_PER_PAGE,
                    staffId = filter.staffId,
                    supplierId = filter.supplierId,
                    startDate = StringUtils.formatLocalDate(filter.startDate),
                    endDate = StringUtils.formatLocalDate(filter.endDate)

                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    val body = response.body!!
                    val endOfPaginationReached = body.content.isEmpty() || body.content.size < ITEMS_PER_PAGE
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

    override fun getRefreshKey(state: PagingState<Int, Import>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}