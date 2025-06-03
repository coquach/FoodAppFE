package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.ImportRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Inventory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface   ImportApi {
    @GET("imports")
    suspend fun getImports(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("staffId") staffId: Long? = null,
        @Query("supplierId") supplierId: Long? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
    ): Response<PageResponse<Import>>

    @POST("imports")
    suspend fun createImport(@Body request: ImportRequest): Response<Import>

    @PUT("imports/{id}")
    suspend fun updateImport(
        @Path("id") id: Long,
        @Body request: ImportRequest,
    ): Response<Import>

    @DELETE("imports/{id}")
    suspend fun deleteImport(@Path("id") id: Long): Response<Unit>


    @GET("inventories")
    suspend fun getInventories(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("ingredientId") ingredientId: Long? = null,
        @Query("expiryDate") expiryDate: String? = null,
        @Query("isOutOfStock") isOutOfStock: Boolean? = null,
    ): Response<PageResponse<Inventory>>

}