package com.example.foodapp.data.remote



import com.example.foodapp.data.dto.response.CategoriesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FoodApi {


    @GET("categories") //demo
    suspend fun getCategories(): Response<CategoriesResponse>





}