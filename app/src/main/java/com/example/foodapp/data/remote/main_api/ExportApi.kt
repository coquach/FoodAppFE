package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.ExportRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Export
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ExportApi {
    @GET("exports")
    suspend fun getExports(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("staffId") staffId: Long? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,

        ): Response<PageResponse<Export>>


    @POST("exports")
    suspend fun createExport(@Body request: ExportRequest): Response<Export>


    @PUT("exports/{id}")
    suspend fun updateExport(
        @Path("id") id: Long,
        @Body request: ExportRequest,
    ): Response<Export>


    @DELETE("exports/{id}")
    suspend fun deleteExport(@Path("id") id: Long): Response<Unit>
}