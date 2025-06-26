package com.example.foodapp.data.remote.main_api

import com.example.foodapp.data.dto.request.IngredientRequest
import com.example.foodapp.data.dto.request.UnitRequest
import com.example.foodapp.data.model.Ingredient
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IngredientApi {
    //Unit
    @GET("units/active")
    suspend fun getActiveUnits(): Response<List<com.example.foodapp.data.model.Unit>>

    @GET("units/inActive")
    suspend fun getHiddenUnits(): Response<List<com.example.foodapp.data.model.Unit>>

    @POST("units")
    suspend fun createUnit(
        @Body request: UnitRequest,
    ): Response<com.example.foodapp.data.model.Unit>

    @PUT("units/{id}")
    suspend fun updateUnit(
        @Path("id") id: Long,
        @Body request: UnitRequest,
    ): Response<com.example.foodapp.data.model.Unit>

    @DELETE("units/{id}")
    suspend fun deleteUnit(
        @Path("id") id: Long,
    ): Response<Unit>

    @PUT("units/set-active/{id}")
    suspend fun recoverUnit(
        @Path("id") id: Long,
        @Body isActive: Boolean,
    ): Response<Unit>


    //Ingredient
    @GET("ingredients/active")
    suspend fun getActiveIngredients(): Response<List<Ingredient>>

    @GET("ingredients/inActive")
    suspend fun getHiddenIngredients(): Response<List<Ingredient>>

    @GET("ingredients/{id}")
    suspend fun getIngredientById(
        @Path("id") id: Long,
    ): Response<Ingredient>

    @POST("ingredients")
    suspend fun createIngredient(
        @Body request: IngredientRequest,
    ): Response<Ingredient>

    @PUT("ingredients/{id}")
    suspend fun updateIngredient(
        @Path("id") id: Long,
        @Body request: IngredientRequest,
    ): Response<Ingredient>

    @DELETE("ingredients/{id}")
    suspend fun deleteIngredient(
        @Path("id") id: Long,
    ): Response<Unit> // 204 No Content

    @PUT("ingredients/set-active/{id}")
    suspend fun setActiveIngredient(
        @Path("id") id: Long,
        @Body isActive: Boolean,
    ): Response<Unit>
}