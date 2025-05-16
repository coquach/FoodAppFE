package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.model.Export
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.data.dto.filter.ExportFilter
import com.se114.foodapp.data.paging.ExportPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportRepository @Inject constructor(
    private val foodApi: FoodApi,
) {

    fun getAllExports(filter: ExportFilter): Flow<PagingData<Export>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                ExportPagingSource(foodApi = foodApi, filter = filter)
            }
        ).flow
    }


}