package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Supplier
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SupplierApi {
    @GET("suppliers")
    suspend fun getSuppliers(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("name") name: String? = null,
        @Query("phone") phone: String? = null,
        @Query("email") email: String? = null,
        @Query("address") address: String? = null,
        @Query("isActive") isActive: Boolean? = null,
    ): Response<PageResponse<Supplier>>

    @POST("suppliers")
    suspend fun createSupplier(
        @Body request: SupplierRequest,
    ): Response<Supplier>

    @PUT("suppliers/{id}")
    suspend fun updateSupplier(
        @Path("id") id: Long,
        @Body request: SupplierRequest,
    ): Response<Supplier>

    @PUT("suppliers/set-active/{id}")
    suspend fun setActiveSupplier(
        @Path("id") id: Long,
        @Body isActive: Boolean,
    ): Response<Unit>

    @DELETE("suppliers/{id}")
    suspend fun deleteSupplier(
        @Path("id") id: Long,
    ): Response<Unit>
}