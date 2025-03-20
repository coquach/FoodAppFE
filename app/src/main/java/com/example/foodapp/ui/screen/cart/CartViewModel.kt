package com.example.foodapp.ui.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.datastore.CartRepository
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    var errorTitle : String = ""
    var errorMessage : String = ""

    private val _uiState = MutableStateFlow<CartState>(CartState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartEvents>()
    val event = _event.asSharedFlow()

    val checkoutDetails = cartRepository.getCheckoutDetails().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CheckoutDetails(0f, 0f, 0f, 0f)
    )


    init {
        getCart()
    }

    private fun getCart() {
        viewModelScope.launch {
            _uiState.value = CartState.Loading
            try {
                val cartItems = cartRepository.getCartItems()

                _uiState.value = CartState.Success(cartItems, checkoutDetails.value)
            } catch (e: Exception) {
                _uiState.value = CartState.Error("Không thể cập nhật số lượng: ${e.message}")
            }
        }
    }

    fun incrementQuantity(cartItem: CartItem) {
        if (cartItem.quantity == 10) // VD số lượng tối đa
            return
        updateItemQuantity(cartItem, cartItem.quantity + 1)
    }

    fun decrementQuantity(cartItem: CartItem) {
        if (cartItem.quantity == 1)
            return
        updateItemQuantity(cartItem, cartItem.quantity - 1)

    }
    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            viewModelScope.launch {
                _uiState.value = CartState.Loading
                try {
                    val currentItems = cartRepository.getCartItems().toMutableList()
                    currentItems.removeAll { it.id == cartItem.id }
                    cartRepository.saveCartItems(currentItems)


                    _uiState.value = CartState.Success(currentItems, checkoutDetails.value)
                } catch (e: Exception) {
                    errorTitle = "Không thể xóa"
                    errorMessage = "Đã xảy ra lỗi khi xóa mặt hàng"
                    _event.emit(CartEvents.OnItemRemoveError)
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


                _uiState.value = CartState.Success(updatedItems, checkoutDetails.value)
            } catch (e: Exception) {
                errorTitle = "Không thể cập nhật số lượng"
                errorMessage = "Đã xảy ra lỗi khi cập nhật số lượng của mặt hàng"
                _event.emit(CartEvents.OnQuantityUpdateError)
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