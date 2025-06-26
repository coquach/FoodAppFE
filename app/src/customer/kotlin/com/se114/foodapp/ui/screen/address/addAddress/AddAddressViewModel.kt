package com.se114.foodapp.ui.screen.address.addAddress


import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.AddressUI
import com.example.foodapp.location.LocationManager
import com.se114.foodapp.domain.use_case.location.GeoCodingUseCase
import com.se114.foodapp.domain.use_case.location.ReverseGeoCodeUseCase
import com.se114.foodapp.ui.screen.address.addAddress.AddAddress.Event.BackToAddressList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val reverseGeoCodeUseCase: ReverseGeoCodeUseCase,
    private val geoCodingUseCase: GeoCodingUseCase,
    private val locationManager: LocationManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddAddress.UiState())
    val uiState: StateFlow<AddAddress.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<AddAddress.Event>()
    val event = _event.receiveAsFlow()


    private val _currentLocation: Flow<Location> = locationManager.getLocation()
    val currentLocation: Flow<Location> = _currentLocation


    private fun reverseGeocode(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val query = "$lat,$lon"
                reverseGeoCodeUseCase(query).collect { result ->
                    when (result) {
                        is ApiResponse.Success -> {
                            _uiState.update {
                                it.copy(address = result.data, isLoading = false)
                            }
                        }
                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(error = result.errorMessage, isLoading = false)
                            }
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update {
                                it.copy(isLoading = true)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Đã xảy ra lỗi khi lấy địa chỉ", isLoading = false)
                }
            }

        }
    }


    private fun geocoding(address: String) {
        viewModelScope.launch {
            try {
                geoCodingUseCase(address).collect { result ->
                    when (result) {
                        is ApiResponse.Success -> {
                            _uiState.update {
                                it.copy(address = result.data, isLoading = false)
                            }

                        }
                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(error = result.errorMessage, isLoading = false)

                            }
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update {
                                it.copy(isLoading = true)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Đã xảy ra lỗi khi lấy địa chỉ", isLoading = false)
                }

            }
        }
    }
    fun onAction(action: AddAddress.Action) {
        when (action) {
            is AddAddress.Action.OnAddAddress -> {
                viewModelScope.launch {
                    _event.send(BackToAddressList(action.address))
                }

            }
            is AddAddress.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(AddAddress.Event.OnBack)
                }
            }

            is AddAddress.Action.OnGeocoding -> {
                geocoding(action.address)
            }
            is AddAddress.Action.OnReverseGeocode -> {
                reverseGeocode(action.lat, action.lon)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        locationManager.stopLocationUpdate()
    }
    
}

object AddAddress {
    data class UiState(
        val isLoading: Boolean = false,
        val address: AddressUI? = null,
        val error: String? = null,
    )

    sealed interface Event {
        data object OnBack : Event
        data class BackToAddressList(val address: AddressUI) : Event
        data object ShowError : Event
    }

    sealed interface Action {
        data class OnAddAddress(val address: AddressUI ) : Action
        data object OnBack : Action
        data class OnReverseGeocode(val lat: Double, val lon: Double) : Action
        data class OnGeocoding(val address: String) : Action
    }
}
