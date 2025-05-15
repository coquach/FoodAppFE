package com.example.foodapp.data.dto.response

data class GeocodingResponse(
    val results: List<Result>
)

data class Result(
    val geometry: Geometry,
    val formatted: String
)

data class Geometry(
    val lat: Double,
    val lng: Double
)
