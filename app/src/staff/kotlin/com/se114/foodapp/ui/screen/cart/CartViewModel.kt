package com.se114.foodapp.ui.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.se114.foodapp.domain.use_case.cart.ClearCartUseCase
import com.se114.foodapp.domain.use_case.cart.GetCartUseCase
import com.se114.foodapp.domain.use_case.cart.GetCheckOutDetailsUseCase
import com.se114.foodapp.domain.use_case.cart.UpdateCartItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val getCheckOutDetailsUseCase: GetCheckOutDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(Cart.UiState())
    val uiState: StateFlow<Cart.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<Cart.Event>()
    val event = _event.receiveAsFlow()

    init {
        getCartItems()
        getCheckoutDetails()
    }
    fun increment(cartItem: CartItem) {
        val current = _uiState.value.quantityMap[cartItem.id] ?: cartItem.quantity
        val newQty = current + 1
        updateQuantity(cartItem, newQty)
    }

    fun decrement(cartItem: CartItem) {
        val current = _uiState.value.quantityMap[cartItem.id] ?: cartItem.quantity
        if (current <= 1) return
        val newQty = current - 1
        updateQuantity(cartItem, newQty)
    }

    fun removeItem() {
        viewModelScope.launch {
            try {
                clearCartUseCase(_uiState.value.selectedItems)
                _uiState.update { it.copy(selectedItems = emptyList()) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Đã xảy ra lỗi khi xóa món ăn") }
                _event.send(Cart.Event.ShowError)
            }
        }
    }

    private fun getCartItems(){
        viewModelScope.launch {
            try {
                getCartUseCase().collect {
                    _uiState.update { it.copy(cartItems = it.cartItems) }
                }
            }catch (e: Exception){
                _uiState.update { it.copy(error = e.message ?: "Đã xảy ra lỗi khi lấy giỏ hàng") }
                _event.send(Cart.Event.ShowError)
            }
        }
    }


    private fun updateQuantity(cartItem: CartItem, newQuantity: Int) {

        _uiState.update {
            it.copy(quantityMap = _uiState.value.quantityMap.toMutableMap().apply {
                put(cartItem.id, newQuantity)
            })
        }
        viewModelScope.launch {
            try {
                updateCartItemQuantityUseCase(cartItem.id, newQuantity)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Đã xảy ra lỗi khi cập nhật số lượng"
                    )
                }
                _event.send(Cart.Event.ShowError)
            }
        }
    }

    private fun toggleSelection(cartItem: CartItem) {
        if (_uiState.value.selectedItems.contains(cartItem)) {
            _uiState.update { it.copy(selectedItems = _uiState.value.selectedItems - cartItem) }
        } else {
            _uiState.update { it.copy(selectedItems = _uiState.value.selectedItems + cartItem) }
        }
    }

    private fun selectAllItems(cartItems: List<CartItem>, isSelectAll: Boolean) {
        _uiState.update { it.copy(selectedItems = emptyList()) }
        if (isSelectAll) _uiState.update { it.copy(selectedItems = cartItems) }
    }

    private fun getCheckoutDetails() {
        viewModelScope.launch {
            try {
                getCheckOutDetailsUseCase.invoke().collect {
                    _uiState.update { it.copy(checkoutDetails = it.checkoutDetails) }
                }
            }catch (e: Exception){
                _uiState.update { it.copy(error = e.message ?: "Đã xảy ra lỗi khi lấy chi tiết đơn hàng") }
                _event.send(Cart.Event.ShowError)
            }
        }
    }

    fun onAction(action: Cart.Action) {
        when (action) {
            is Cart.Action.OnCheckOut -> {
                viewModelScope.launch {
                    _event.send(Cart.Event.NavigateToCheckout)
                }
            }
            is Cart.Action.OnIncreaseCartItem -> {
                increment(action.cartItem)
            }

            is Cart.Action.OnDecreaseCartItem -> {
                decrement(action.cartItem)

            }

            is Cart.Action.OnToggleSelection -> {
                toggleSelection(action.cartItem)
            }

            is Cart.Action.OnSelectAll -> {
                selectAllItems(_uiState.value.cartItems, action.isSelectAll)
            }

            is Cart.Action.OnRemoveItem -> {
                removeItem()
            }

            is Cart.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(Cart.Event.OnBack)
                }
            }
        }
    }
}


object Cart {
    data class UiState(
        val isLoading: Boolean = false,
        val cartItems: List<CartItem> = emptyList(),
        val checkoutDetails: CheckoutDetails = CheckoutDetails(
            BigDecimal(0),
            BigDecimal(0),
            BigDecimal(0),
            BigDecimal(0)
        ),
        val selectedItems: List<CartItem> = emptyList(),
        val quantityMap: Map<Long, Int> = emptyMap(),
        val error: String? = null,
    )

    sealed interface Event {
        data object OnBack : Event
        data object NavigateToCheckout : Event
        data object ShowError : Event
    }

    sealed interface Action {
        data object OnCheckOut : Action
        data class OnIncreaseCartItem(val cartItem: CartItem) : Action
        data class OnDecreaseCartItem(val cartItem: CartItem) : Action
        data class OnToggleSelection(val cartItem: CartItem) : Action
        data class OnSelectAll(val isSelectAll: Boolean) : Action
        data object OnRemoveItem : Action
        data object OnBack : Action

    }
}