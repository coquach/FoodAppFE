package com.se114.foodapp.data.repository

import com.example.foodapp.BuildConfig
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.response.GeocodingResponse
import com.example.foodapp.data.remote.OpenCageApi
import com.se114.foodapp.domain.repository.OpenCageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OpenCageRepoImpl @Inject constructor(
    private val openCageApi: OpenCageApi
) : OpenCageRepository {
    private val apiKey = BuildConfig.OPEN_CAGE_API_KEY
    override fun geoCoding(address: String): Flow<ApiResponse<GeocodingResponse>> {
        return apiRequestFlow {
            openCageApi.geocode(query = address, apiKey = apiKey)
        }
    }

    override fun reverseGeoCode(
       latLng: String
    ): Flow<ApiResponse<GeocodingResponse>> {
        return apiRequestFlow {
            openCageApi.reverseGeocode(latlng = latLng, apiKey = apiKey)
        }
    }
}