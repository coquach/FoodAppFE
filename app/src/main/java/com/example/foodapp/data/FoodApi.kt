package com.example.foodapp.data

import retrofit2.http.GET

interface FoodApi {
    @GET
    suspend fun getFoods(): List<String>
}