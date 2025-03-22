package com.example.foodapp.ui.screen.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<AddressState>(AddressState.Loading)
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddressEvent?>()
    val event = _event.asSharedFlow()

    fun getAddress() {

    }

    fun onAddAddressClicked() {
        viewModelScope.launch {
            _event.emit(AddressEvent.NavigateToAddAddress)
        }
    }

    sealed class AddressState {
        object Loading : AddressState()
        data class Success(val addresses: List<Address>) : AddressState()
        data class Error(val message: String) : AddressState()
    }

    sealed class AddressEvent {
        data class NavigateToEditAddress(val address: Address) : AddressEvent()
        data object NavigateToAddAddress : AddressEvent()
    }
}