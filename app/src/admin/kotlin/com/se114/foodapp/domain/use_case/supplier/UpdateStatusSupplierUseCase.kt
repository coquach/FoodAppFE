package com.se114.foodapp.domain.use_case.supplier

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.domain.repository.SupplierRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateStatusSupplierUseCase @Inject constructor(
    private val supplierRepository: SupplierRepository,
) {
    operator fun invoke(supplierId: Long, isActive: Boolean) = flow<ApiResponse<Unit>> {

        try {
            supplierRepository.setActiveSupplier(supplierId, isActive).collect { emit(it) }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))

        }
    }.flowOn(Dispatchers.IO)
}