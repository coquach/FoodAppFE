package com.se114.foodapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.datastore.CartRepository
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
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
    private val cartRepository: CartRepository

) : ViewModel() {

    val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvents>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    var categories = emptyList<Category>()

    val cartSize: StateFlow<Int> = cartRepository.getCartSize()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        getCategories()
        getPopularRestaurants()
    }

    private fun getCategories() {
        viewModelScope.launch {
            val response = safeApiCall {
                foodApi.getCategories()
            }
            when (response) {
                is ApiResponse.Success -> {
                    categories = (response.body.data as? List<Category>) ?: emptyList()
                    _uiState.value = HomeState.Success

                }

                is ApiResponse.Error -> {
                    _uiState.value = HomeState.Empty
                }

                else -> {
                    _uiState.value = HomeState.Empty
                }
            }
        }
    }

    fun getPopularRestaurants() {

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