package com.se114.foodapp.data.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.remote.main_api.SupplierApi
import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.se114.foodapp.domain.repository.SupplierRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SupplierRepoImpl @Inject constructor(
    private val supplierApi: SupplierApi
) : SupplierRepository {
    override fun getSuppliers(filter: SupplierFilter): Flow<ApiResponse<List<Supplier>>> {
       return apiRequestFlow {
           supplierApi.getSuppliers(
               name = filter.name,
               phone = filter.phone,
               email = filter.email,
               address = filter.address,
               isActive = filter.isActive
           )
       }
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