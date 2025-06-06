package com.se114.foodapp.domain.use_case.report

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.data.model.DailyReport
import com.se114.foodapp.domain.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetDailyReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    operator fun invoke(year: Int, month: Int) = flow<ApiResponse<List<DailyReport>>> {
        try {
            reportRepository.getDailyReport(year, month).collect{
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã có lỗi xảy ra khi tải danh sách báo cáo ngày", 999))
        }
    }.flowOn(Dispatchers.IO)
}