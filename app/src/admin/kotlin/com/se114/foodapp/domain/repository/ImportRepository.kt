package com.se114.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.ImportRequest
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Inventory
import com.se114.foodapp.data.dto.filter.ImportFilter
import com.se114.foodapp.data.dto.filter.InventoryFilter
import kotlinx.coroutines.flow.Flow

interface ImportRepository {
    fun getImports(filter: ImportFilter): Flow<PagingData<Import>>
    fun createImport(request: ImportRequest): Flow<ApiResponse<Import>>
    fun updateImport(id: Long, request: ImportRequest): Flow<ApiResponse<Import>>
    fun deleteImport(id: Long): Flow<ApiResponse<Unit>>
    fun getInventories(filter: InventoryFilter): Flow<PagingData<Inventory>>
}