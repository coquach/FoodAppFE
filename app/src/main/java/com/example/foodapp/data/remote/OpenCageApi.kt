package com.example.foodapp.data.remote

import com.example.foodapp.data.dto.response.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenCageApi {
    @GET("geocode/v1/json")
    suspend fun geocode(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "vi",
        @Query("limit") limit: Int = 1
    ): Response<GeocodingResponse>

    @GET("geocode/v1/json")
    suspend fun reverseGeocode(
        @Query("q") latlng: String, // Ví dụ: "10.7769,106.7009"
        @Query("key") apiKey: String,
        @Query("language") language: String = "vi",
        @Query("limit") limit: Int = 1
    ): Response<GeocodingResponse>
}
