package com.se114.foodapp.ui.screen.menu

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.repository.MenuItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MenuViewModel
@Inject constructor(
    private val menuItemRepository: MenuItemRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuState>(MenuState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<MenuEvents>()
    val event = _event.asSharedFlow()

    private val _menuItemsAvailable = MutableStateFlow<PagingData<MenuItem>>(PagingData.empty())
    val menuItemsAvailable = _menuItemsAvailable

    private val _menuItemsHidden = MutableStateFlow<PagingData<MenuItem>>(PagingData.empty())
    val menuItemsHidden = _menuItemsHidden

    init {
        refreshMenus()
    }

    fun refreshMenus() {
        viewModelScope.launch {

                menuItemRepository.getMenuItemsByFilter(MenuItemFilter(isAvailable = true)).cachedIn(viewModelScope)
                    .collect { _menuItemsAvailable.value = it }


                menuItemRepository.getMenuItemsByFilter(MenuItemFilter(isAvailable = false)).cachedIn(viewModelScope)
                    .collect { _menuItemsHidden.value = it }
            }

    }

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
        data object Success :
            MenuState()

        data object Error : MenuState()
    }

    sealed class MenuEvents {
        data class ShowErrorMessage(val message: String) : MenuEvents()
        data object ShowDeleteDialog : MenuEvents()
    }
}