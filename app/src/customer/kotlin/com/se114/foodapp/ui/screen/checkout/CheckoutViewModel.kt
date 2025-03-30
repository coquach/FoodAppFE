package com.se114.foodapp.ui.screen.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.datastore.CartRepository
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.se114.foodapp.ui.screen.cart.CartViewModel.CartEvents
import com.se114.foodapp.ui.screen.cart.CartViewModel.CartState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {
    var errorTitle: String = ""
    var errorMessage: String = ""

    private val _uiState = MutableStateFlow<CheckoutState>(CheckoutState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CheckoutEvents>()
    val event = _event.asSharedFlow()

    private val checkoutDetails = cartRepository.getCheckoutDetails().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CheckoutDetails(0f, 0f, 0f, 0f)
    )

    init {
        getCart()
    }

    private fun getCart() {
        viewModelScope.launch {
            try {
                combine(
                    cartRepository.getCartItems(),
                    checkoutDetails
                ) { cartItems, checkout ->
                    CheckoutState.Success(cartItems, checkout)
                }.collectLatest { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                errorTitle = "Lỗi"
                errorMessage = "Không thể tải hàng: ${e.message}"
                _uiState.value = CheckoutState.Error("Không thể tải hàng: ${e.message}")
            }
        }
    }

    fun onAddressClicked() {
        viewModelScope.launch {
            _event.emit(CheckoutEvents.OnAddress)
        }

    }

    fun onConfirmClicked() {
        viewModelScope.launch {
            _event.emit(CheckoutEvents.OrderSuccess("12345"))
            val allCartItems = cartRepository.getCartItems().firstOrNull() ?: emptyList()
            cartRepository.clearCartItems(allCartItems)
        }
    }



    sealed class CheckoutState {
        data object Nothing : CheckoutState()
        data object Loading : CheckoutState()
        data class Success(val cartItems: List<CartItem>, val checkoutDetails: CheckoutDetails) :
            CheckoutState()

        data class Error(val message: String) : CheckoutState()
    }

    sealed class CheckoutEvents {
        data object ShowErrorDialog : CheckoutEvents()
        data object OnCheckOut : CheckoutEvents()
        data object OnAddress: CheckoutEvents()
        data class OrderSuccess(val orderId: String?) : CheckoutEvents()

    }
}