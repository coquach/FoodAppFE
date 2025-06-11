package com.se114.foodapp.ui.screen.address

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.foodapp.data.model.AddressUI
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.navigation.MyAddressList
import com.se114.foodapp.domain.use_case.address.AddAddressUseCase
import com.se114.foodapp.domain.use_case.address.DeleteAddressUseCase
import com.se114.foodapp.domain.use_case.address.GetAddressUseCase
import com.se114.foodapp.ui.screen.address.AddressList.Event.BackToCheckout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val isCheckout = savedStateHandle.toRoute<MyAddressList>().isCheckout

    private val _uiState = MutableStateFlow(AddressList.UiState(isCheckout = isCheckout))
    val uiState: StateFlow<AddressList.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<AddressList.Event>()
    val event = _event.receiveAsFlow()

    init {
        getAddress()
    }

    private fun getAddress() {
        viewModelScope.launch {
            getAddressUseCase().collect { result ->
                when (result) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(addressesState = AddressList.AddressesState.Loading) }
                    }

                    is FirebaseResult.Success -> {
                        _uiState.update { it.copy(addressesState = AddressList.AddressesState.Success, addresses = result.data) }
                    }

                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(addressesState = AddressList.AddressesState.Error(result.error)) }
                    }

                }
            }

        }

    }

    private fun addAddress(address: AddressUI) {
        viewModelScope.launch {
            addAddressUseCase.invoke(address).collect { result ->
                when (result) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(addressesState = AddressList.AddressesState.Loading) }
                    }

                    is FirebaseResult.Success -> {
                        _uiState.update {
                            it.copy(
                              addressesState = AddressList.AddressesState.Success,
                                addresses = it.addresses + address
                            )
                        }

                    }

                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(error = result.error) }
                        _event.send(AddressList.Event.ShowError)
                    }

                }
            }
        }
    }

    private fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            deleteAddressUseCase.invoke(addressId).collect { result ->
                when (result) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(addressesState = AddressList.AddressesState.Loading) }
                    }

                    is FirebaseResult.Success -> {
                        _uiState.update {
                            it.copy(
                                addressesState = AddressList.AddressesState.Success,
                                addresses = it.addresses.filter { it.id != addressId })
                        }
                    }

                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(error = result.error) }
                        _event.send(AddressList.Event.ShowError)
                    }
                }
            }
        }
    }

    fun onAction(action: AddressList.Action) {
        when (action) {
            is AddressList.Action.OnAddAddress -> {
                viewModelScope.launch {
                    _event.send(AddressList.Event.NavigateToAddAddress)
                }
            }

            is AddressList.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(AddressList.Event.OnBack)
                }
            }

            is AddressList.Action.OnSelectAddress -> {
                _uiState.update { it.copy(selectedAddress = action.id) }
            }

            is AddressList.Action.OnDeleteAddress -> {
                deleteAddress(action.id)
            }

            is AddressList.Action.OnSelectToBackCheckOut -> {
                viewModelScope.launch {
                    _event.send(BackToCheckout(action.address))
                }
            }

            is AddressList.Action.AddAddress -> {
                addAddress(action.address)
            }
        }
    }

}

object AddressList {
    data class UiState(
        val isLoading: Boolean = false,
        val addresses: List<AddressUI> = emptyList(),
        val addressesState: AddressesState? = null,
        val error: String? = null,
        val selectedAddress: String? = null,
        val isCheckout: Boolean = false,
    )

    sealed interface AddressesState {
        data object Success : AddressesState
        data class Error(val message: String) : AddressesState
        data object Loading : AddressesState
    }

    sealed interface Event {
        data object ShowError : Event
        data class BackToCheckout(val address: AddressUI) : Event
        data object NavigateToAddAddress : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data class AddAddress(val address: AddressUI) : Action
        data object OnBack : Action
        data object OnAddAddress : Action
        data class OnSelectAddress(val id: String?) : Action
        data class OnDeleteAddress(val id: String) : Action
        data class OnSelectToBackCheckOut(val address: AddressUI) : Action
    }
}