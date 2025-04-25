package com.se114.foodapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.repository.MenuItemRepository

import com.se114.foodapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val cartRepository: CartRepository,
    private val menuItemRepository: MenuItemRepository

) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvents>()
    val navigationEvent = _navigationEvent.asSharedFlow()


    val cartSize: StateFlow<Int> = cartRepository.getCartSize()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _menuItems = MutableStateFlow<PagingData<MenuItem>>(PagingData.empty())
    val menuItems = _menuItems
    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {

            menuItemRepository.getMenuItemsByFilter(MenuItemFilter(isAvailable = true)).cachedIn(viewModelScope)
                .collect { _menuItems.value = it }

        }

    }


    fun onNotificationClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvents.NavigateToNotification)
        }
    }


    sealed class HomeState {
        data object Loading : HomeState()
        data object Empty : HomeState()
        data object Success : HomeState()
    }

    sealed class HomeNavigationEvents {
        data object NavigateToDetail : HomeNavigationEvents()
        data object NavigateToNotification : HomeNavigationEvents()
    }
}