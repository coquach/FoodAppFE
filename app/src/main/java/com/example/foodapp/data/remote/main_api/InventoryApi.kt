package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Inventory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InventoryApi {
    @GET("inventories")
    suspend fun getInventories(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id",
        @Query("order") order: String = "desc",
        @Query("ingredientId") ingredientId: Long? = null,
        @Query("isExpired") isExpired: Boolean? = null,
        @Query("isOutOfStock") isOutOfStock: Boolean? = null,
    ): Response<PageResponse<Inventory>>
}