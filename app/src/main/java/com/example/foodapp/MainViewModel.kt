package com.example.foodapp

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()



    fun handleDeeplink(uri: Uri) {
        val oobCode = uri.getQueryParameter("oobCode")
        val mode = uri.getQueryParameter("mode")
        if (!oobCode.isNullOrBlank() && !mode.isNullOrBlank()) {
            viewModelScope.launch {
                _event.emit(HomeEvent.NavigateToResetPassword(oobCode, mode))
            }
        }
    }


    fun navigateToOrderDetail(order: Order) {
        viewModelScope.launch {
            _event.emit(HomeEvent.NavigateToOrderDetail(order))
        }
    }

    sealed class HomeEvent {
        data class NavigateToOrderDetail(val order: Order) : HomeEvent()
        data class NavigateToResetPassword(val oobCode: String, val mode: String) : HomeEvent()


    }
}