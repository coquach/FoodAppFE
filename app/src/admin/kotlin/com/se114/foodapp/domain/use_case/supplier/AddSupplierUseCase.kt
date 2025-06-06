package com.se114.foodapp.domain.use_case.supplier

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.model.Supplier
import com.se114.foodapp.domain.repository.SupplierRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AddSupplierUseCase @Inject constructor(
    private val supplierRepository: SupplierRepository,
) {
    operator fun invoke(supplier: Supplier) = flow<ApiResponse<Supplier>> {
        emit(ApiResponse.Loading)
        try {
            val request = SupplierRequest(
                name = supplier.name,
                phone = supplier.phone,
                email = supplier.email,
                address = supplier.address
            )
            supplierRepository.createSupplier(request).collect { emit(it) }

        } catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))

        }

    }.flowOn(Dispatchers.IO)
}