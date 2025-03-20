package com.example.foodapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.models.response.Category
import com.example.foodapp.data.remote.ApiResponse
import com.example.foodapp.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val foodApi: FoodApi) :ViewModel()  {

    val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvents?>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    var categories = emptyList<Category>()

    init {
        getCategories()
        getPopularRestaurants()
    }
    
    private fun getCategories() {
        viewModelScope.launch {
            val response = safeApiCall {
                foodApi.getCategories()
            }
            when(response) {
                is ApiResponse.Success -> {
                    categories = response.data.data
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


    sealed class HomeState {
        data object Loading : HomeState()
        data object Empty : HomeState()
        data object Success : HomeState()
    }

    sealed class HomeNavigationEvents {
        data object NavigateDetail : HomeNavigationEvents()
    }
}