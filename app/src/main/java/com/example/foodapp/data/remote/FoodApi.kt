package com.example.foodapp.data.remote



import com.example.foodapp.data.dto.request.RegisterRequest
import com.example.foodapp.data.dto.response.CategoriesResponse
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.dto.response.RegisterResponse
import com.se114.foodapp.data.model.Staff
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {




    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>


    //admin
    @GET("staffs")
    suspend fun getStaffs(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc"
    ): PageResponse<Staff>

    @Multipart
    @POST("staffs")
    suspend fun createStaff(
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageUrl: MultipartBody.Part? = null
    ): Response<Staff>

    @Multipart
    @PUT("staffs/{id}")
    suspend fun updateStaff(
        @Path("id") id: Long,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imageUrl: MultipartBody.Part? = null
    ): Response<Staff>

    @DELETE("staffs/{id}")
    suspend fun deleteStaff(@Path("id") id: Long): Response<Unit>

    @POST("staffs/caculate-salary")
    suspend fun calculateSalary(): Response<Map<String, Int>>

    @GET("staffs/total-salary")
    suspend fun getTotalSalary(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<Map<String, Double>>
}