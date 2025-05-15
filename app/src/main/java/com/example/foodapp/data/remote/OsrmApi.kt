package com.example.foodapp.data.remote

import com.example.foodapp.data.dto.response.RouteRequest
import com.example.foodapp.data.dto.response.RouteResponse
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OsrmApi {
    @POST("v2/directions/driving-car/geojson")
    suspend fun getRoute(
        @Header("Authorization") apiKey: String,
        @Body body: RouteRequest
    ): Response<RouteResponse>
}
