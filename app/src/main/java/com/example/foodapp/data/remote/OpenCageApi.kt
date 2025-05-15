package com.example.foodapp.data.remote

interface OpenCageApi {
    @GET("geocode/v1/json")
    suspend fun geocode(
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "vi",
        @Query("limit") limit: Int = 1
    ): Response<GeocodingResponse>
}
