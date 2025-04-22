package com.se114.foodapp.ui.screen.food_item_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.datastore.CartRepository
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FoodDetailsViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FoodDetailsState>(FoodDetailsState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<FoodDetailsEvent>()
    val event = _event.asSharedFlow()

    private val _quantity = MutableStateFlow<Int>(1)
    val quantity = _quantity.asStateFlow()

    fun incrementQuantity() {
        if (quantity.value == 10) return
        _quantity.value += 1
    }

    fun decrementQuantity() {
        if (quantity.value == 1) return
        _quantity.value -= 1
    }

    fun addToCart(foodItem: FoodItem) {
        viewModelScope.launch {
            _uiState.value = FoodDetailsState.Loading
            try {
                val currentItems = cartRepository.getCartItems().first().toMutableList()
                val existingItemIndex = currentItems.indexOfFirst { it.menuItemId.id == foodItem.id }

                if (existingItemIndex != -1) {
                    val updatedItem = currentItems[existingItemIndex].copy(
                        quantity = quantity.value
                    )
                    currentItems[existingItemIndex] = updatedItem
                    cartRepository.saveCartItems(currentItems)
                    _event.emit(FoodDetailsEvent.OnItemAlreadyInCart)
                } else {
                    val newItem = CartItem(
                        id = UUID.randomUUID().toString(),
                        menuItemId = foodItem,
                        quantity = quantity.value,
                        userId = "user_123",
                        addedAt = System.currentTimeMillis().toString()
                    )
                    currentItems.add(newItem)
                    cartRepository.saveCartItems(currentItems)
                    _event.emit(FoodDetailsEvent.OnAddToCart)
                }

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