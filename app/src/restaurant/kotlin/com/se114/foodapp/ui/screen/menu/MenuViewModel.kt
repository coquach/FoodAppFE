package com.se114.foodapp.ui.screen.menu

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.model.MenuItem
import com.se114.foodapp.data.repository.MenuItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class MenuViewModel
@Inject constructor(
    private val menuItemRepository: MenuItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuState>(MenuState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<MenuEvents>()
    val event = _event.asSharedFlow()

    val menuItemsAvailable =
        menuItemRepository.getMenuItemsByFilter(MenuItemFilter(isAvailable = true))
            .cachedIn(viewModelScope)
    val menuItemsHidden =
        menuItemRepository.getMenuItemsByFilter(MenuItemFilter(isAvailable = false))
            .cachedIn(viewModelScope)


    private val _selectedItems = mutableStateListOf<MenuItem>()
    val selectedItems: List<MenuItem> get() = _selectedItems

    fun toggleSelection(menuItem: MenuItem) {
        if (_selectedItems.contains(menuItem)) {
            _selectedItems.remove(menuItem)
        } else {
            _selectedItems.add(menuItem)
        }
    }

    fun selectAllItems(menuItems: List<MenuItem>, isSelectAll: Boolean) {
        _selectedItems.clear()
        if (isSelectAll) _selectedItems.addAll(menuItems)
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
        data class Success(val cartItems: List<MenuItem>) :
            MenuState()

        data class Error(val message: String) : MenuState()
    }

    sealed class MenuEvents {
        data object NavigateToDetail : MenuEvents()
        data object ShowDeleteDialog : MenuEvents()
    }
}