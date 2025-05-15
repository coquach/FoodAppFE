package com.se114.foodapp.ui.screen.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.service.AccountService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressListViewModel @Inject constructor(
    private val accountService: AccountService,
) : ViewModel() {

    private val _state = MutableStateFlow<AddressState>(AddressState.Loading)
    val state = _state.asStateFlow()

    private val _event = Channel<AddressEvent>()
    val event = _event.receiveAsFlow()

    private val userId = accountService.currentUserId

    private val _addressList = MutableStateFlow<List<Address>>(emptyList())
    val addressList = _addressList.asStateFlow()

    init {
        getAddress()
    }


    fun getAddress() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = AddressState.Loading
            try {
                val firestore = FirebaseFirestore.getInstance()
                val addressesRef = firestore.collection("users")
                    .document(userId ?: throw Exception("User not logged in"))
                    .collection("addresses")

                addressesRef.get()
                    .addOnSuccessListener { querySnapshot ->
                        val addresses = querySnapshot.documents.mapNotNull { document ->
                            if (document.id == "init") return@mapNotNull null
                            val id = document.getString("id")
                            val formatted = document.getString("formatted")
                            val lat = document.getDouble("lat")
                            val lon = document.getDouble("lon")


                            if (id != null && formatted != null && lat != null && lon != null) {
                                Address(id, formatted, lat, lon)
                            } else {
                                null
                            }
                        }

                        _addressList.value = addresses
                        _state.value = AddressState.Success
                    }
                    .addOnFailureListener { exception ->
                        _state.value = AddressState.Error(exception.message ?: "Lỗi khi nhận địa chỉ")
                    }
            } catch (e: Exception) {
                    _state.value = AddressState.Error(e.message ?: "Lỗi khi nhận địa chỉ")
            }


        }

    }

    fun addAddress(address: Address) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = AddressState.Loading
            try {
                val firestore = FirebaseFirestore.getInstance()
                val addressRef = firestore.collection("users")
                    .document(userId ?: throw Exception("User not logged in"))
                    .collection("addresses")
                    .document(address.id) // lấy ID từ Address

                val addressMap = hashMapOf(
                    "id" to address.id,
                    "formatted" to address.formatAddress,
                    "lat" to address.latitude,
                    "lon" to address.longitude
                )

                addressRef.set(addressMap)
                    .addOnSuccessListener {
                        _addressList.update {
                            it + address
                        }
                        _state.value = AddressState.Success
                        getAddress()
                    }
                    .addOnFailureListener { e ->
                        _state.value = AddressState.Error(e.message ?: "Lỗi khi thêm địa chỉ")
                    }

            } catch (e: Exception) {
                _state.value = AddressState.Error(e.message ?: "Lỗi khi thêm địa chỉ")
            }
        }
    }



    fun deleteAddress(addressId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = AddressState.Loading
            try {
                val firestore = FirebaseFirestore.getInstance()
                val addressRef = firestore.collection("users")
                    .document(userId ?: throw Exception("User not logged in"))
                    .collection("addresses")
                    .document(addressId)

                addressRef.delete()
                    .addOnSuccessListener {
                        _addressList.update {
                            it.filter { it.id != addressId }
                        }
                        _state.value = AddressState.Success
                        getAddress()

                    }
                    .addOnFailureListener { e ->
                        _state.value = AddressState.Error(e.message ?: "Lỗi khi xóa địa chỉ")
                    }

            } catch (e: Exception) {
                _state.value = AddressState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _event.send(AddressEvent.OnBack)
        }
    }

    fun onAddAddressClicked() {
        viewModelScope.launch {
            _event.send(AddressEvent.NavigateToAddAddress)
        }
    }
    fun navigateToCheckout(address: String) {
        viewModelScope.launch {
            _event.send(AddressEvent.NavigateToCheckout(address))
        }
    }

    sealed class AddressState {
        data object Loading : AddressState()
        data object Success : AddressState()
        data class Error(val message: String) : AddressState()
    }

    sealed class AddressEvent {
        data object OnBack : AddressEvent()
        data object NavigateToAddAddress : AddressEvent()
        data class NavigateToCheckout(val address: String) : AddressEvent()
    }
}