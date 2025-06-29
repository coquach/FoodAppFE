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
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodTableApi {
    @GET("food-tables")
    suspend fun getFoodTables(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "asc",
        @Query("active") active: Boolean?=null,
        @Query("status") status: String?=null,

    ): Response<PageResponse<FoodTable>>

    @POST("food-tables")
    suspend fun createFoodTable(@Body request: FoodTableRequest): Response<FoodTable>

    @PUT("food-tables/{id}")
    suspend fun updateFoodTable(
        @Body request: FoodTableRequest,
        @Path("id") id: Int,
    ): Response<FoodTable>

    @PATCH("food-tables/{id}/toggle-status")
    suspend fun updateFoodTableStatus(
        @Path("id") id: Int,
    ): Response<Unit>

    @DELETE("food-tables/{id}")
    suspend fun deleteFoodTable(
        @Path("id") id: Int,
    ): Response<Unit>

    @PATCH("food-tables/{id}/orders")
    suspend fun createOrderForFoodTable(
        @Path("id") id: Int,
    ): Response<FoodTable>


}