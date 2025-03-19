package com.example.foodapp.ui.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.datastore.CartRepository
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CartState>(CartState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartEvents>()
    val event = _event.asSharedFlow()

    init {
        getCart()
    }

    private fun getCart() {
        viewModelScope.launch {
            _uiState.value = CartState.Loading
            try {
                val cartItems = cartRepository.getCartItems()
                val checkoutDetails = cartRepository.getCheckoutDetails()
                _uiState.value = CartState.Success(cartItems, checkoutDetails)
            } catch (e: Exception) {
                _uiState.value = CartState.Error("Không thể cập nhật số lượng: ${e.message}")
            }
        }
    }

    fun incrementQuantity(cartItem: CartItem, quantity: Int) {
        if (cartItem.quantity == 10) // VD số lượng tối đa
            return
        updateItemQuantity(cartItem, quantity + 1)
    }

    fun decrementQuantity(cartItem: CartItem, quantity: Int) {
        if (cartItem.quantity == 1)
            return
        updateItemQuantity(cartItem, quantity - 1)

    }
    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            viewModelScope.launch {
                _uiState.value = CartState.Loading
                try {
                    val currentItems = cartRepository.getCartItems().toMutableList()
                    currentItems.removeAll { it.id == cartItem.id }
                    cartRepository.saveCartItems(currentItems)

                    val checkoutDetails = cartRepository.getCheckoutDetails()
                    _uiState.value = CartState.Success(currentItems, checkoutDetails)
                } catch (e: Exception) {
                    _uiState.value = CartState.Error("Không thể xoá sản phẩm: ${e.message}")
                }
            }
        }
    }


    private fun updateItemQuantity(cartItem: CartItem, quantity: Int) {
        viewModelScope.launch {
            _uiState.value = CartState.Loading
            try {
                val currentItems = cartRepository.getCartItems().toMutableList()
                val updatedItems = currentItems.map {
                    if (it.id == cartItem.id) it.copy(quantity = quantity) else it
                }
                cartRepository.saveCartItems(updatedItems)

                val checkoutDetails = cartRepository.getCheckoutDetails()
                _uiState.value = CartState.Success(updatedItems, checkoutDetails)
            } catch (e: Exception) {
                _uiState.value = CartState.Error("Không thể cập nhật số lượng: ${e.message}")
            }
        }
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
        data object OnQuantityUpdateError: CartEvents()
        data object OnItemRemoveError : CartEvents()
    }
}