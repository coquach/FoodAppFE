package com.se114.foodapp.ui.screen.address.addAddress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Address
import com.example.foodapp.location.GeocodingRepository
import com.example.foodapp.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val geocodingRepository: GeocodingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddAddressState>(AddAddressState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddAddressEvent>()
    val event = _event.asSharedFlow()

    private val _address = MutableStateFlow<Address?>(null)
    val address = _address.asStateFlow()

    fun getLocation() = locationManager.getLocation()

    private val addressCache = mutableMapOf<Pair<Double, Double>, Address?>()

    fun reverseGeoCode(lat: Double, lon: Double) {
        viewModelScope.launch {
            _address.value = null
            try {
                val key = lat to lon
                if (addressCache.containsKey(key)) {
                    _address.value = addressCache[key]
                } else {
                    val address = geocodingRepository.getAddressFromCoordinates(lat, lon)
                    _address.value = address
                    if (address != null) {
                        addressCache[key] = address
                    }
                }
                _uiState.value = AddAddressState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AddAddressState.Error("Lỗi lấy địa chỉ: ${e.message}")
            }
        }
    }

    fun onAddAddressClicked() {

    }


    sealed class AddAddressEvent {
        data object NavigateToAddressList : AddAddressEvent()

    }

    sealed class AddAddressState {
        data object Loading : AddAddressState()
        data object Success : AddAddressState()
        data object AddressStoring : AddAddressState()
        data class Error(val message: String) : AddAddressState()
    }
}
