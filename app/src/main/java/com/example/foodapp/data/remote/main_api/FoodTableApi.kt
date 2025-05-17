package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.FoodTableRequest
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.FoodTable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface FoodTableApi {
    @GET("food-tables")
    suspend fun getFoodTables(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
    ): Response<PageResponse<FoodTable>>

    @POST("food-tables")
    suspend fun createFoodTable(@Body request: FoodTableRequest): Response<FoodTable>

    @PUT("food-tables/{id}")
    suspend fun updateFoodTable(
        @Body request: FoodTableRequest,
        @Query("id") id: Long,
    ): Response<FoodTable>

    @PATCH("food-tables/{id}/status")
    suspend fun updateFoodTableStatus(
        @Body status: Map<String, Boolean>,
        @Query("id") id: Long,
    ): Response<Unit>

    @DELETE("food-tables/{id}")
    suspend fun deleteFoodTable(
        @Query("id") id: Long,
    ): Response<Unit>


}