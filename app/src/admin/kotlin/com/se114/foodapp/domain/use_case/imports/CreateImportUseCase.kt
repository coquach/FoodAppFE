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
import java.time.LocalDateTime
import javax.inject.Inject

class CreateImportUseCase @Inject constructor(
    private val importRepository: ImportRepository,
) {
    operator fun invoke(import: Import, importDetails: List<ImportDetailUIModel>) =
        flow<ApiResponse<Import>> {
            emit(ApiResponse.Loading)
            try {
                val request = ImportRequest(
                    supplierId = import.supplierId,
                    importDate = StringUtils.formatDateTime(input = LocalDateTime.now())!!,
                    importDetails = importDetails.map {
                        ImportDetailRequest(
                            ingredientId = it.ingredient?.id,
                            expiryDate = StringUtils.formatLocalDate(it.expiryDate),
                            productionDate = StringUtils.formatLocalDate(it.productionDate),
                            quantity = it.quantity,
                            cost = it.cost
                        )
                    }
                )
                importRepository.createImport(request).collect {
                    emit(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi tạo đơn nhập hàng", 999))
            }
        }.flowOn(Dispatchers.IO)
}