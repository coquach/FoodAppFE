package com.example.foodapp.ui.screen.cart

import androidx.lifecycle.ViewModel
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<CartState>(CartState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartEvents>()
    val event = _event.asSharedFlow()

    fun incrementQuantity(cartItem: CartItem, quantity: Int) {

    }

    fun decrementQuantity(cartItem: CartItem, quantity: Int) {

    }
    fun removeItem(cartItem: CartItem) {

    }

    fun checkout() {

    }

    sealed class CartState {
        data object Nothing: CartState()
        data object Loading: CartState()
        data class Success(val cartItems:List<CartItem>, val checkoutDetails: CheckoutDetails): CartState()
        data class Error(val message: String): CartState()

    }

    sealed class CartEvents {
        data object ShowErrorDialog: CartEvents()
        data object OnCheckOut: CartEvents()
    }
}