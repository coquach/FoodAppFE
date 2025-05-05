package com.example.foodapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import java.io.IOException

class InventoryPagingSource(
    private val foodApi: FoodApi,
    private val filter: InventoryFilter,
) : PagingSource<Int, Inventory>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Inventory> {
        val currentPage = params.key ?: 0
        return try {
            val response = safeApiCall {
                foodApi.getInventories(
                    page = currentPage,
                    size = ITEMS_PER_PAGE,
                    ingredientId = filter.ingredientId,
                    expiryDate = filter.expiryDate,
                    isOutOfStock = filter.isOutOfStock
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

    override fun getRefreshKey(state: PagingState<Int, Inventory>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }
}