package com.example.foodapp.data

import com.example.foodapp.data.models.request.SignUpRequest
import com.example.foodapp.data.models.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("/food") //demo
    suspend fun getFoods(): List<String>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse
}