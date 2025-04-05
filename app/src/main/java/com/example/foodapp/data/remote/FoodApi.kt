package com.example.foodapp.data.remote

import com.example.foodapp.data.dto.request.LoginRequest
import com.example.foodapp.data.dto.request.RefreshTokenRequest
import com.example.foodapp.data.dto.request.SignUpRequest
import com.example.foodapp.data.dto.response.AuthResponse
import com.example.foodapp.data.dto.response.BaseResponse

import com.example.foodapp.data.dto.response.CategoriesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FoodApi {
    @GET("categories") //demo
    suspend fun getCategories(): Response<CategoriesResponse>

    @POST("auth/signup") //demo
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("auth/login") //demo
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("access-token") token: String): Response<BaseResponse>


}