package com.example.foodapp.ui.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<NotificationEvent>()
    val event = _event.asSharedFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    fun navigateToOrderDetail(orderID: String) {
        viewModelScope.launch {
            _event.emit(NotificationEvent.NavigateToOrderDetail(orderID))
        }
    }

    fun readNotification(notification: Notification) {

    }

    fun getNotifications() {

    }

    sealed class NotificationEvent {
        data class NavigateToOrderDetail(val orderID: String) : NotificationEvent()
    }

    sealed class NotificationState {
        data object Loading : NotificationState()
        data class Success(val data: List<Notification>) : NotificationState()
        data class Error(val message: String) : NotificationState()
    }
}