package com.se114.foodapp.domain.use_case.location

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.response.GeocodingResponse
import com.example.foodapp.data.model.Address

import com.se114.foodapp.domain.repository.OpenCageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class GeoCodingUseCase @Inject constructor(
    private val openCageRepository: OpenCageRepository,
) {
    operator fun invoke(address: String) = flow<ApiResponse<Address>> {
        try {
            openCageRepository.geoCoding(address).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        val formatted = result.data.results.firstOrNull()?.formatted
                        val geometry = result.data.results.firstOrNull()?.geometry
                        if (geometry != null && formatted != null) {
//
                            emit(
                                ApiResponse.Success(
                                    Address(
                                        formatAddress = address,
                                        latitude = geometry.lat,
                                        longitude = geometry.lng
                                    )
                                )
                            )
                        } else throw IllegalStateException("Không tìm thấy địa chỉ trong kết quả reverseGeocode!")
                    }
                    is ApiResponse.Failure -> {
                        emit(ApiResponse.Failure(result.errorMessage, result.code))
                    }
                    is ApiResponse.Loading -> {
                        emit(ApiResponse.Loading)
                    }

                }
            }

        } catch (e: Exception) {
            throw e
        }
    }
}