package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.request.ImportRequest
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.remote.main_api.ImportApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.data.dto.filter.ImportFilter
import com.se114.foodapp.data.dto.filter.InventoryFilter
import com.se114.foodapp.data.paging.ImportPagingSource
import com.se114.foodapp.data.paging.InventoryPagingSource
import com.se114.foodapp.domain.repository.ImportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportRepoImpl @Inject constructor(
    private val importApi: ImportApi
) : ImportRepository {

    fun getAllImports(filter: ImportFilter): Flow<PagingData<Import>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                ImportPagingSource(importApi = importApi, filter = filter)
            }
        ).flow
    }

    override fun getImports(filter: ImportFilter): Flow<PagingData<Import>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                ImportPagingSource(importApi = importApi, filter = filter)
            }
        ).flow
    }

    override fun createImport(request: ImportRequest): Flow<ApiResponse<Import>> {
        return apiRequestFlow {
            importApi.createImport(request = request)
        }
    }

    override fun updateImport(
        id: Long,
        request: ImportRequest,
    ): Flow<ApiResponse<Import>> {
        return apiRequestFlow {
            importApi.updateImport(id = id, request = request)
        }
    }

    override fun deleteImport(id: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            importApi.deleteImport(id = id)
        }
    }

    override fun getInventories(filter: InventoryFilter): Flow<PagingData<Inventory>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                InventoryPagingSource(importApi = importApi, filter = filter)
            }
        ).flow
    }


}