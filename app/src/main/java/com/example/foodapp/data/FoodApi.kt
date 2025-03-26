package com.example.foodapp.data

import com.example.foodapp.data.dto.request.AddToCartRequest
import com.example.foodapp.data.dto.request.LoginRequest
import com.example.foodapp.data.dto.request.SignUpRequest
import com.example.foodapp.data.dto.response.AddToCartResponse
import com.example.foodapp.data.dto.response.LoginResponse
import com.example.foodapp.data.dto.response.CategoriesResponse
import com.example.foodapp.data.remote.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("categories") //demo
    suspend fun getCategories(): Response<ApiResponse<CategoriesResponse>>

    @POST("auth/signup") //demo
    suspend fun signUp(@Body request: SignUpRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/login") //demo
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>


}