package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.remote.main_api.SupplierApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.data.paging.SupplierPagingSource
import com.se114.foodapp.domain.repository.SupplierRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SupplierRepoImpl @Inject constructor(
    private val supplierApi: SupplierApi
) : SupplierRepository {
    override fun getSuppliers(filter: SupplierFilter): Flow<PagingData<Supplier>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true),
            pagingSourceFactory = {
                SupplierPagingSource(supplierApi = supplierApi, filter = filter)
            }
        ).flow
    }

    override fun createSupplier(request: SupplierRequest): Flow<ApiResponse<Supplier>> {
        return apiRequestFlow {
            supplierApi.createSupplier(request)
        }
    }

    override fun updateSupplier(
        id: Long,
        request: SupplierRequest,
    ): Flow<ApiResponse<Supplier>> {
        return apiRequestFlow {
            supplierApi.updateSupplier(id, request)

        }
    }

    override fun setActiveSupplier(
        id: Long,
        isActive: Boolean,
    ): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            supplierApi.setActiveSupplier(id, isActive)
        }
    }
}