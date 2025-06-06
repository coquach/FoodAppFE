package com.se114.foodapp.data.remote

import com.se114.foodapp.data.model.DailyReport
import com.se114.foodapp.data.model.MenuReport
import com.se114.foodapp.data.model.MonthlyReport
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApi {
    @GET("reports/monthly")
    suspend fun getMonthlyReport(
        @Query("fromYear") fromYear: Int,
        @Query("fromMonth") fromMonth: Int,
        @Query("toYear") toYear: Int,
        @Query("toMonth") toMonth: Int
    ): Response<List<MonthlyReport>>

    @GET("reports/daily")
    suspend fun getDailyReport(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<List<DailyReport>>

    @GET("reports/menu-details")
    suspend fun getMenuReport(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<List<MenuReport>>

}