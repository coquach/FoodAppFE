package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.model.Export
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.data.dto.filter.ExportFilter
import com.se114.foodapp.data.dto.request.ExportRequest
import com.se114.foodapp.data.paging.ExportPagingSource
import com.se114.foodapp.data.remote.ExportApi
import com.se114.foodapp.domain.repository.ExportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportRepoImpl @Inject constructor(
    private val exportApi: ExportApi,
) : ExportRepository {


    override fun getExports(filter: ExportFilter): Flow<PagingData<Export>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                ExportPagingSource(exportApi = exportApi, filter = filter)
            }
        ).flow
    }

    override fun createExport(request: ExportRequest): Flow<ApiResponse<Export>> {
        return apiRequestFlow {
            exportApi.createExport(request)
        }
    }

    override fun updateExport(
        id: Long,
        request: ExportRequest,
    ): Flow<ApiResponse<Export>> {
        return apiRequestFlow {
            exportApi.updateExport(id, request)
        }
    }

    override fun deleteExport(id: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            exportApi.deleteExport(id)
        }
    }


}