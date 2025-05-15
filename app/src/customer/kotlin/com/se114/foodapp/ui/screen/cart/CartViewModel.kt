package com.se114.foodapp.ui.screen.cart

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.se114.foodapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    var errorTitle: String = ""
    var errorMessage: String = ""



    private val _event = MutableSharedFlow<CartEvents>()
    val event = _event.asSharedFlow()


    private val _cartItems = cartRepository.cartItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems
    private val _checkoutDetails = cartRepository.checkoutDetailsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CheckoutDetails(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0))
    )
    val checkoutDetails: StateFlow<CheckoutDetails> = _checkoutDetails

    private val _selectedItems = mutableStateListOf<CartItem>()
    val selectedItems: List<CartItem> get() = _selectedItems

    private val _quantityMap = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val quantityMap = _quantityMap.asStateFlow()

    fun increment(cartItem: CartItem) {
        val current = _quantityMap.value[cartItem.id] ?: cartItem.quantity
        val newQty = current + 1
        updateQuantity(cartItem, newQty)
    }

    fun decrement(cartItem: CartItem) {
        val current = _quantityMap.value[cartItem.id] ?: cartItem.quantity
        if (current <= 1) return
        val newQty = current - 1
        updateQuantity(cartItem, newQty)
    }

    fun removeItem() {
        viewModelScope.launch {
                try {
                    cartRepository.clearCartItems(_selectedItems)
                    _selectedItems.clear()
                } catch (e: Exception) {
                    errorTitle = "Không thể xóa"
                    errorMessage = "Đã xảy ra lỗi khi xóa mặt hàng"
                    _event.emit(CartEvents.OnItemRemoveError)
                }
            }
    }


    private fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        // Cập nhật StateFlow để UI phản ứng ngay
        _quantityMap.value = _quantityMap.value.toMutableMap().apply {
            put(cartItem.id!!, newQuantity)
        }

        // Cập nhật database
        viewModelScope.launch {
            try {
                cartRepository.updateItemQuantity(cartItem.id!!, newQuantity)
            } catch (_: Exception) {

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






    sealed class CartEvents {
        data object ShowErrorDialog : CartEvents()
        data object OnCheckOut : CartEvents()
        data object OnQuantityUpdateError : CartEvents()
        data object OnItemRemoveError : CartEvents()
        data object NavigateToCheckOut : CartEvents()
    }
}