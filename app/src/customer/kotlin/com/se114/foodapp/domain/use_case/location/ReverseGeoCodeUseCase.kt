package com.se114.foodapp.domain.use_case.location

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.AddressUI
import com.se114.foodapp.domain.repository.OpenCageRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReverseGeoCodeUseCase @Inject constructor(
    private val openCageRepository: OpenCageRepository,
) {
    operator fun invoke(latLng: String) = flow<ApiResponse<AddressUI>> {
        try {
            openCageRepository.reverseGeoCode(latLng).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        val formatted = result.data.results.firstOrNull()?.formatted
                        val geometry = result.data.results.firstOrNull()?.geometry
                        if (geometry != null && formatted != null) {
                            emit(
                                ApiResponse.Success(
                                    AddressUI(
                                        formatAddress = formatted,
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