package com.se114.foodapp.data.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.se114.foodapp.data.model.DailyReport
import com.se114.foodapp.data.model.MenuReport
import com.se114.foodapp.data.model.MonthlyReport
import com.se114.foodapp.data.remote.ReportApi
import com.se114.foodapp.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportRepoImpl @Inject constructor(
    private val reportApi: ReportApi
) : ReportRepository {
    override fun getMonthlyReport(fromYear: Int, fromMonth: Int, toYear: Int, toMonth: Int): Flow<ApiResponse<List<MonthlyReport>>> {
        return apiRequestFlow {
            reportApi.getMonthlyReport(fromYear, fromMonth, toYear, toMonth)
        }
    }

    override fun getDailyReport(year: Int, month: Int): Flow<ApiResponse<List<DailyReport>>> {
        return apiRequestFlow {
            reportApi.getDailyReport(year, month)
        }
    }

    override fun getMenuReport(year: Int, month: Int): Flow<ApiResponse<List<MenuReport>>> {
        return apiRequestFlow {
            reportApi.getMenuReport(year, month)

        }
    }
}