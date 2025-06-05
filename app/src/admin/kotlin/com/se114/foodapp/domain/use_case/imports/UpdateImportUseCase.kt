package com.se114.foodapp.domain.use_case.imports

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.ImportDetailRequest
import com.example.foodapp.data.dto.request.ImportRequest
import com.example.foodapp.data.model.Import
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.domain.repository.ImportRepository
import com.se114.foodapp.ui.screen.warehouse.imports.ImportDetailUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateImportUseCase @Inject constructor(
    private val importRepository: ImportRepository,
) {
    operator fun invoke(import: Import, importDetails: List<ImportDetailUIModel>) =
        flow<ApiResponse<Import>> {
            emit(ApiResponse.Loading)
            try {
                val request = ImportRequest(
                    supplierId = import.supplierId,
                    staffId = import.staffId,
                    importDate = StringUtils.formatDateTime(import.importDate)!!,
                    importDetails = importDetails.map {
                        ImportDetailRequest(
                            id = it.id,
                            ingredientId = it.ingredient?.id,
                            expiryDate = StringUtils.formatDateTime(it.expiryDate),
                            productionDate = StringUtils.formatDateTime(it.productionDate),
                            quantity = it.quantity,
                            cost = it.cost
                        )
                    }
                )
                val importId = import.id!!
                importRepository.updateImport(importId, request).collect {
                    emit(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(
                    ApiResponse.Failure(
                        e.message ?: "Đã xảy ra lỗi khi cập nhật đơn nhập hàng",
                        999
                    )
                )
            }
        }.flowOn(Dispatchers.IO)

}