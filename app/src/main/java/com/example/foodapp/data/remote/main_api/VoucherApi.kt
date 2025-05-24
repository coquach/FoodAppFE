package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.VoucherRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Voucher
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface VoucherApi {
    @GET("vouchers")
    suspend fun getVouchers(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Voucher>>

    @GET("vouchers/customer")
    suspend fun getVouchersForCustomer(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<Voucher>>

    @POST("vouchers")
    suspend fun createVouchers(
        @Body request: VoucherRequest,
    ): Response<Voucher>

    @PUT("vouchers/{id}")
    suspend fun updateVoucher(
        @Path("id") id: Long,
        @Body request: VoucherRequest,
    ): Response<Voucher>

    @DELETE("vouchers/{id}")
    suspend fun deleteVoucher(@Path("id") id: Long): Response<Unit>

}