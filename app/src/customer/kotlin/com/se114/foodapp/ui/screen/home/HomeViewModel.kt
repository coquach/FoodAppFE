package com.se114.foodapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.FoodFilter
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.repository.FoodRepository

import com.se114.foodapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
    private val foodRepository: FoodRepository

) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvents>()
    val navigationEvent = _navigationEvent.asSharedFlow()


    val cartSize: StateFlow<Int> = cartRepository.getCartSize()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _foods = MutableStateFlow<PagingData<Food>>(PagingData.empty())
    val foods: StateFlow<PagingData<Food>> = _foods

    private val _menuId = MutableStateFlow<Long>(1)
    val menuId: StateFlow<Long> = _menuId

    fun setMenuId(menuId: Long) {
        _menuId.value = menuId
    }
    private val foodFlows = mutableMapOf<Long, Flow<PagingData<Food>>>()

    fun getFoodsByMenuId(menuId: Long): Flow<PagingData<Food>> {
        return foodFlows.getOrPut(menuId) {
            foodRepository.getFoodsByMenuId(menuId)
                .cachedIn(viewModelScope)
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