package com.se114.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.data.dto.request.ExportRequest
import com.example.foodapp.data.model.Export
import com.se114.foodapp.data.dto.filter.ExportFilter
import kotlinx.coroutines.flow.Flow

interface ExportRepository {
    fun getExports(filter: ExportFilter): Flow<PagingData<Export>>
    fun createExport(request: ExportRequest): Flow<ApiResponse<Export>>
    fun updateExport(id: Long, request: ExportRequest): Flow<ApiResponse<Export>>
    fun deleteExport(id: Long): Flow<ApiResponse<Unit>>
}