package com.se114.foodapp.data.paging

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Export
import com.example.foodapp.data.paging.ApiPagingSource
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.data.remote.ExportApi
import com.se114.foodapp.data.dto.filter.ExportFilter
import kotlinx.coroutines.flow.Flow

class ExportPagingSource(
    private val exportApi: ExportApi,
    private val filter: ExportFilter,
) : ApiPagingSource<Export>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Export>>> {
        return apiRequestFlow {
            exportApi.getExports(
                page = page,
                size = size,
                order = filter.order,
                sortBy = filter.sortBy,
                startDate = StringUtils.formatLocalDate(filter.startDate),
                endDate = StringUtils.formatLocalDate(filter.endDate),
            )
        }
    }
}