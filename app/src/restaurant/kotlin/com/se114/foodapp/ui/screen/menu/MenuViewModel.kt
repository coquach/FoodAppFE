package com.se114.foodapp.ui.screen.menu

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<MenuState>(MenuState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<MenuEvents>()
    val event = _event.asSharedFlow()

    private val _selectedItems = mutableStateListOf<FoodItem>()
    val selectedItems: List<FoodItem> get() = _selectedItems

    fun toggleSelection(foodItem: FoodItem) {
        if (_selectedItems.contains(foodItem)) {
            _selectedItems.remove(foodItem)
        } else {
            _selectedItems.add(foodItem)
        }
    }

    fun selectAllItems(foodItems: List<FoodItem>, isSelectAll: Boolean) {
        _selectedItems.clear()
        if (isSelectAll) _selectedItems.addAll(foodItems)
    }

    fun removeItem() {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
              e.printStackTrace()
            }
        }
    }

    fun onRemoveClicked() {
        viewModelScope.launch {
            _event.emit(MenuEvents.ShowDeleteDialog)
        }
    }
    sealed class MenuState {
        data object Nothing : MenuState()
        data object Loading : MenuState()
        data class Success(val cartItems: List<FoodItem>) :
            MenuState()

        data class Error(val message: String) : MenuState()
    }

    sealed class MenuEvents {
        data object NavigateToDetail : MenuEvents()
        data object ShowDeleteDialog : MenuEvents()
    }
}