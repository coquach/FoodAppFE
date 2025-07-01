package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Staff
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface StaffApi {
    @GET("staffs")
    suspend fun getStaffs(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "desc",
        @Query("fullName") fullName: String? = null,
        @Query("gender") gender: String? = null,
        @Query("active") active: Boolean = true,
    ): Response<PageResponse<Staff>>

    @Multipart
    @POST("staffs")
    suspend fun createStaff(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part avatar: MultipartBody.Part? = null,
    ): Response<Staff>

    @Multipart
    @PUT("staffs/{id}")
    suspend fun updateStaff(
        @Path("id") id: Long,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part avatar: MultipartBody.Part? = null,
    ): Response<Staff>

    @DELETE("staffs/{id}")
    suspend fun deleteStaff(@Path("id") id: Long): Response<Unit>

//    @POST("staffs/caculate-salary")
//    suspend fun calculateSalary(): Response<Map<String, Int>>
//
//    @GET("staffs/total-salary")
//    suspend fun getTotalSalary(
//        @Query("month") month: Int,
//        @Query("year") year: Int,
//    ): Response<Map<String, Double>>

    @PATCH("staffs/{id}/terminate")
    suspend fun terminateStaff(@Path("id") id: Long): Response<Staff>

}