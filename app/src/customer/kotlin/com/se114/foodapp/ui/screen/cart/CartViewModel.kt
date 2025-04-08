package com.se114.foodapp.ui.screen.cart

import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    var errorTitle: String = ""
    var errorMessage: String = ""

    private val _uiState = MutableStateFlow<CartState>(CartState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartEvents>()
    val event = _event.asSharedFlow()

    private val checkoutDetails = cartRepository.getCheckoutDetails().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CheckoutDetails(0f, 0f, 0f, 0f)
    )

    private val _selectedItems = mutableStateListOf<CartItem>()
    val selectedItems: List<CartItem> get() = _selectedItems



    init {
        getCart()
    }

    private fun getCart() {
        viewModelScope.launch {
            _uiState.value = CartState.Loading
            try {
                combine(
                    cartRepository.getCartItems(),
                    checkoutDetails
                ) { cartItems, checkout ->
                    CartState.Success(cartItems, checkout)
                }.collectLatest { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                errorTitle = "Lỗi"
                errorMessage = "Không thể tải được giỏ hàng: ${e.message}"
                _uiState.value = CartState.Error("Không thể tải được giỏ hàng: ${e.message}")
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

    fun removeItem() {
        viewModelScope.launch {
                try {
                    cartRepository.clearCartItems(_selectedItems)
                    _selectedItems.clear()
                    getCart()
                } catch (e: Exception) {
                    errorTitle = "Không thể xóa"
                    errorMessage = "Đã xảy ra lỗi khi xóa mặt hàng"
                    _event.emit(CartEvents.OnItemRemoveError)
                }
            }
    }


    private fun updateItemQuantity(cartItem: CartItem, quantity: Int) {
        viewModelScope.launch {
            try {
                val currentItems = cartRepository.getCartItems().first().toMutableList()
                val index = currentItems.indexOfFirst { it.id == cartItem.id }

                if (index != -1) {
                    val updatedItem = cartItem.copy(quantity = quantity)
                    currentItems[index] = updatedItem
                    cartRepository.saveCartItems(currentItems) // ✅ Lưu danh sách mới
                    getCart()
                }
            } catch (e: Exception) {
                errorTitle = "Không thể cập nhật số lượng"
                errorMessage = "Đã xảy ra lỗi khi cập nhật số lượng của mặt hàng"
                _event.emit(CartEvents.OnQuantityUpdateError)
            }
        }
    }

    fun toggleSelection(cartItem: CartItem) {
        if (_selectedItems.contains(cartItem)) {
            _selectedItems.remove(cartItem)
        } else {
            _selectedItems.add(cartItem)
        }
    }

    fun selectAllItems(cartItems: List<CartItem>, isSelectAll: Boolean) {
        _selectedItems.clear()
        if (isSelectAll) _selectedItems.addAll(cartItems)
    }


    fun checkout() {
        viewModelScope.launch {
            _event.emit(CartEvents.NavigateToCheckOut)
        }
    }

    fun onAddressClicked() {
        viewModelScope.launch {
            _event.emit(CartEvents.OnAddress)
        }

    }

    sealed class CartState {
        data object Nothing : CartState()
        data object Loading : CartState()
        data class Success(val cartItems: List<CartItem>, val checkoutDetails: CheckoutDetails) :
            CartState()

        data class Error(val message: String) : CartState()

    }

    sealed class CartEvents {
        data object ShowErrorDialog : CartEvents()
        data object OnCheckOut : CartEvents()
        data object OnQuantityUpdateError : CartEvents()
        data object OnItemRemoveError : CartEvents()
        data object OnAddress: CartEvents()
        data object NavigateToCheckOut : CartEvents()
    }
}