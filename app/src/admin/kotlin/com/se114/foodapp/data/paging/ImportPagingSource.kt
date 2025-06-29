package com.se114.foodapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow

import com.example.foodapp.data.dto.response.PageResponse

import com.example.foodapp.data.model.Import
import com.example.foodapp.data.paging.ApiPagingSource
import com.example.foodapp.data.remote.main_api.ImportApi
import com.example.foodapp.utils.StringUtils

import com.se114.foodapp.data.dto.filter.ImportFilter
import kotlinx.coroutines.flow.Flow


class ImportPagingSource(
    private val importApi: ImportApi,
    private val filter: ImportFilter,
) : ApiPagingSource<Import>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Import>>> {
        return apiRequestFlow {
            importApi.getImports(
                page = page,
                size = size,
                order = filter.order,
                sortBy = filter.sortBy,
                supplierName = filter.supplierName,
                startDate = StringUtils.formatLocalDate(filter.startDate),
                endDate = StringUtils.formatLocalDate(filter.endDate),

            )
        }
    }

}