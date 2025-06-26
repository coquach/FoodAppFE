package com.example.foodapp.data.dto.response


data class OsrmRouteResponse(
    val routes: List<OsrmRoute>,
)


data class OsrmRoute(
    val geometry: OsrmGeometry? = null,
    val distance: Double, // đơn vị: meters
    val duration: Double, // đơn vị: seconds
)


data class OsrmGeometry(
    val coordinates: List<List<Double>>
)