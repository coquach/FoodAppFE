package com.se114.foodapp.ui.screen.menu_item_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.CartItem

import com.example.foodapp.data.model.MenuItem
import com.se114.foodapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class MenuDetailsViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FoodDetailsState>(FoodDetailsState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<FoodDetailsEvent>()
    val event = _event.asSharedFlow()

    private val cartItems = cartRepository.cartItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _quantity = MutableStateFlow<Int>(1)
    val quantity = _quantity.asStateFlow()

    fun incrementQuantity() {
        _quantity.value += 1
    }

    fun decrementQuantity() {
        if (quantity.value == 1) return
        _quantity.value -= 1
    }

    fun addToCart(menuItem: MenuItem) {
        viewModelScope.launch {
            _uiState.value = FoodDetailsState.Loading
            try {
                val current = cartItems.value.toMutableList()
                val index = current.indexOfFirst { it.id == menuItem.id }

                if (index != -1) {
                    val updatedItem = current[index].copy(
                        quantity = quantity.value
                    )
                    current[index] = updatedItem
                    _event.emit(FoodDetailsEvent.OnItemAlreadyInCart)
                } else {
                    val newItem = CartItem(
                        id = menuItem.id,
                        name = menuItem.name,
                        menuName = menuItem.menuName,
                        quantity = quantity.value,
                        price = menuItem.price,
                        menuId = menuItem.menuId,
                    )
                    current.add(newItem)

                    _event.emit(FoodDetailsEvent.OnAddToCart)
                }
                cartRepository.saveCartItems(current)
                _uiState.value = FoodDetailsState.Nothing

            } catch (e: Exception) {
                _uiState.value = FoodDetailsState.Error(e.message ?: "Không thể thêm vào giỏ hàng.")
                _event.emit(FoodDetailsEvent.ShowErrorDialog(e.message ?: "Có lỗi xảy ra."))
            }
        }
    }


    fun goToCart() {
        viewModelScope.launch {
            _event.emit(FoodDetailsEvent.GoToCart)
            _quantity.value = 1
        }
    }


    sealed class FoodDetailsState {
        data object Nothing : FoodDetailsState()
        data object Loading : FoodDetailsState()
        data class Error(val message: String) : FoodDetailsState()
    }

    sealed class FoodDetailsEvent {
        data class ShowErrorDialog(val message: String) : FoodDetailsEvent()
        data object OnAddToCart : FoodDetailsEvent()
        data object OnItemAlreadyInCart : FoodDetailsEvent()
        data object GoToCart : FoodDetailsEvent()

    }
}