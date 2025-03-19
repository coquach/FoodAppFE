package com.example.foodapp.data

import com.example.foodapp.data.dto.request.AddToCartRequest
import com.example.foodapp.data.dto.request.LoginRequest
import com.example.foodapp.data.dto.request.SignUpRequest
import com.example.foodapp.data.dto.response.AddToCartResponse
import com.example.foodapp.data.dto.response.AuthResponse
import com.example.foodapp.data.dto.response.CategoriesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("/categories") //demo
    suspend fun getCategories(): Response<CategoriesResponse>

    @POST("/auth/signup") //demo
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("/auth/login") //demo
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/cart")
    suspend fun addToCart(@Body cartRequest: AddToCartRequest): Response<AddToCartResponse>

}