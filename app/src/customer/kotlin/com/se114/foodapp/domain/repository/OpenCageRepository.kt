package com.se114.foodapp.domain.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.response.GeocodingResponse
import kotlinx.coroutines.flow.Flow

interface OpenCageRepository {
    fun geoCoding(address: String): Flow<ApiResponse<GeocodingResponse>>
    fun reverseGeoCode(latLng: String): Flow<ApiResponse<GeocodingResponse>>
}