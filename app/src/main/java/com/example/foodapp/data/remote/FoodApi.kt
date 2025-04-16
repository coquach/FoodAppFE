package com.example.foodapp.data.remote



import com.example.foodapp.data.dto.request.RegisterRequest
import com.example.foodapp.data.dto.response.CategoriesResponse
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.dto.response.RegisterResponse
import com.se114.foodapp.data.model.Staff
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

}