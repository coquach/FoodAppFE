package com.se114.foodapp.ui.screen.address.addAddress

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.BuildConfig
import com.example.foodapp.data.dto.ApiResponse

import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.remote.OpenCageApi
import com.example.foodapp.data.remote.OsrmApi

import com.example.foodapp.location.LocationManager
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val openCageApi: OpenCageApi,
    private val orsApi: OsrmApi,
    private val locationManager: LocationManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddAddressState>(AddAddressState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<AddAddressEvent>()
    val event = _event.receiveAsFlow()

    private val _address = MutableStateFlow<Address>(
        Address(
            formatAddress = "",
            latitude = null,
            longitude = null
        )
    )
    val address = _address.asStateFlow()




    private val apiKey = BuildConfig.OPEN_CAGE_API_KEY

    private val _currentLocation: Flow<Location> = locationManager.getLocation()
    val currentLocation: Flow<Location> = _currentLocation



    fun reverseGeocode(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val query = "$lat,$lon"
                Log.d("Debug", "LatLng = $query")
                val response = safeApiCall { openCageApi.reverseGeocode(query, apiKey) }
                when (response) {
                    is ApiResponse.Success -> {
                        val address = response.body?.results?.firstOrNull()?.formatted
                        val geometry = response.body?.results?.firstOrNull()?.geometry
                        if (address != null && geometry != null) {
                            _address.update {
                                it.copy(
                                    formatAddress = address,
                                    latitude = geometry.lat,
                                    longitude = geometry.lng
                                )
                            }

                        } else throw IllegalStateException("Không tìm thấy địa chỉ trong kết quả reverseGeocode!")
                    }

                    is ApiResponse.Error -> {
                        Log.e("Geocode", "Lỗi reverse geocoding: ${response.message}")

                    }

                    else -> {
                        Log.e("Geocode", "Lỗi reverse geocoding: Không xác định")

                    }
                }
            } catch (e: Exception) {
                Log.e("Geocode", "Lỗi reverse geocoding", e)
            }
        }
    }

    fun geocoding(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = safeApiCall { openCageApi.geocode(address, apiKey) }
                when (response) {
                    is ApiResponse.Success -> {
                        val formatted = response.body?.results?.firstOrNull()?.formatted
                        val geometry = response.body?.results?.firstOrNull()?.geometry
                        if (geometry != null && formatted != null) {
                            _address.update {
                                it.copy(
                                    formatAddress = formatted,
                                    latitude = geometry.lat,
                                    longitude = geometry.lng
                                )
                            }
                        } else throw IllegalStateException("Không tìm thấy tọa độ trong kết quả geocoding!")

                    }

                    is ApiResponse.Error -> {
                        Log.e("Geocode", "Lỗi geocoding: ${response.message}")

                    }

                    else -> {
                        Log.e("Geocode", "Lỗi geocoding: Không xác định")

                    }
                }
            } catch (e: Exception) {
                Log.e("Geocode", "Lỗi  geocoding", e)

            }
        }
    }




    fun onAddAddressClicked() {
        viewModelScope.launch {
            _event.send(AddAddressEvent.NavigateToAddressList)
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopLocationUpdate()
    }

    sealed class AddAddressEvent {
        data object NavigateToAddressList : AddAddressEvent()

    }

    sealed class AddAddressState {
        data object Loading : AddAddressState()
        data object Success : AddAddressState()
        data class Error(val message: String) : AddAddressState()
    }
}
