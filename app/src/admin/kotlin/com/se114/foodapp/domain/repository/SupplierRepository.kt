package com.se114.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.model.Supplier

import kotlinx.coroutines.flow.Flow

interface SupplierRepository {
    fun getSuppliers(filter: SupplierFilter): Flow<PagingData<Supplier>>
    fun createSupplier(request: SupplierRequest): Flow<ApiResponse<Supplier>>
    fun updateSupplier(id: Long, request: SupplierRequest): Flow<ApiResponse<Supplier>>
    fun setActiveSupplier(id: Long, isActive: Boolean): Flow<ApiResponse<Unit>>

}