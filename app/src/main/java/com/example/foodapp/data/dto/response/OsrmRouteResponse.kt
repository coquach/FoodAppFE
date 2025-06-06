package com.example.foodapp.data.dto.response

data class OsrmRouteResponse(
    val routes: List<OsrmRoute>
)

data class OsrmRoute(
    val geometry: OsrmGeometry
)

data class OsrmGeometry(
    val coordinates: List<List<Double>>
)