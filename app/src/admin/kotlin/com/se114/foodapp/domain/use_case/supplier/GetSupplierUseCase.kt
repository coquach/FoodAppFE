package com.se114.foodapp.domain.use_case.supplier

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Supplier
import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.se114.foodapp.domain.repository.SupplierRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetSupplierUseCase @Inject constructor(
    private val supplierRepository: SupplierRepository
) {
    operator fun invoke(filter: SupplierFilter) = flow<ApiResponse<List<Supplier>>> {
        try {
            supplierRepository.getSuppliers(filter).collect{
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã có lỗi xảy ra trong quá trình lấy danh sách nhà cung cấp", 999))
        }
    }.flowOn(
        Dispatchers.IO)
}