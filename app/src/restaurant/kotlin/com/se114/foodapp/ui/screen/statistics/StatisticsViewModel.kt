package com.se114.foodapp.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor() : ViewModel() {
    val _uiState = MutableStateFlow<StatisticsState>(StatisticsState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<StatisticsEvents>()
    val event = _event.asSharedFlow()


    fun onNotificationClicked() {
        viewModelScope.launch {
            _event.emit(StatisticsEvents.NavigateToNotification)
        }
    }

    sealed class StatisticsState {
        data object Loading : StatisticsState()
        data object Empty : StatisticsState()
        data object Success : StatisticsState()
    }

    sealed class StatisticsEvents {
        data object NavigateToDetail : StatisticsEvents()
        data object NavigateToNotification : StatisticsEvents()
    }
}