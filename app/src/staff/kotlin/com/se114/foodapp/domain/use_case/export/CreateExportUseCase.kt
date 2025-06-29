package com.se114.foodapp.domain.use_case.export

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.data.dto.request.ExportDetailRequest
import com.se114.foodapp.data.dto.request.ExportRequest
import com.example.foodapp.data.model.Export
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.domain.repository.ExportRepository
import com.se114.foodapp.ui.screen.export.export_detail.ExportDetailUIModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateExportUseCase @Inject constructor(
    private val exportRepository: ExportRepository
) {
    operator fun invoke(export: Export, exportDetails: List<ExportDetailUIModel>) = flow<ApiResponse<Export>> {
        try {
            emit(ApiResponse.Loading)
            val request = ExportRequest(
                exportDate = StringUtils.formatLocalDate(export.exportDate)!!,
                exportDetails = exportDetails.map {
                    ExportDetailRequest(
                        inventoryId = it.inventoryId,
                        quantity = it.quantity,
                    )
                }
            )
            exportRepository.createExport(request).collect{
                emit(it)
            }

        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã có lỗi xảy ra khi tạo đơn xuất", 999))
        }
    }.flowOn(Dispatchers.IO)
}