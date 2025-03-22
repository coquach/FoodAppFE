package com.example.foodapp.ui.screen.address.addAddress

import androidx.lifecycle.ViewModel
import com.example.foodapp.location.LocationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(val locationManager: LocationManager) : ViewModel() {
    private val _uiState = MutableStateFlow<AddAddressState>(AddAddressState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddAddressEvent>()
    val event = _event.asSharedFlow()

    fun getLocation() = locationManager.getLocation()


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
