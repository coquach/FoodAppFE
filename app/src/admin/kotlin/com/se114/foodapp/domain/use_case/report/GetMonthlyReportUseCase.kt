package com.se114.foodapp.domain.use_case.report

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.data.model.MonthlyReport
import com.se114.foodapp.domain.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetMonthlyReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    operator fun invoke(fromYear: Int, fromMonth: Int, toYear: Int, toMonth: Int) = flow<ApiResponse<List<MonthlyReport>>> {
        try {
            reportRepository.getMonthlyReport(fromYear, fromMonth, toYear, toMonth).collect{
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã có lỗi xảy ra khi tải danh sách báo cáo tháng", 999))
        }
    }.flowOn(Dispatchers.IO)


}