package com.se114.foodapp.domain.use_case.export

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.domain.repository.ExportRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteExportUseCase @Inject constructor(
    private val exportRepository: ExportRepository,
) {
    operator fun invoke(exportId: Long) = flow<ApiResponse<Unit>> {
        try {
            exportRepository.deleteExport(exportId).collect {
                emit(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã có lỗi xảy ra khi xóa đơn xuất", 999))

        }
    }
}