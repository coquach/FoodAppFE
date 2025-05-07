package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.data.dto.filter.ImportFilter
import com.se114.foodapp.data.paging.ImportPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRepository @Inject constructor(
    private val foodApi: FoodApi,
) {

    fun getAllImports(filter: ImportFilter): Flow<PagingData<Import>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ImportPagingSource(foodApi = foodApi, filter = filter)
            }
        ).flow
    }


}