package com.se114.foodapp.domain.repository

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.data.model.DailyReport
import com.se114.foodapp.data.model.MenuReport
import com.se114.foodapp.data.model.MonthlyReport
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getMonthlyReport(fromYear: Int, fromMonth: Int, toYear: Int, toMonth: Int): Flow<ApiResponse<List<MonthlyReport>>>
    fun getDailyReport(year: Int, month: Int): Flow<ApiResponse<List<DailyReport>>>
    fun getMenuReport(year: Int, month: Int): Flow<ApiResponse<List<MenuReport>>>

}