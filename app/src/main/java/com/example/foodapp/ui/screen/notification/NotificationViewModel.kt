package com.example.foodapp.ui.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Notification
import com.example.foodapp.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = Channel<NotificationEvent>()
    val event get() = _event.receiveAsFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    fun navigateToOrderDetail(it: Order) {
        viewModelScope.launch {
            _event.send(NotificationEvent.NavigateToOrderDetail(it))
        }
    }

    fun readNotification(notification: Notification) {

    }

    fun getNotifications() {

    }

    sealed class NotificationEvent {
        data class NavigateToOrderDetail(val order: Order) : NotificationEvent()
    }

    sealed class NotificationState {
        data object Loading : NotificationState()
        data class Success(val data: List<Notification>) : NotificationState()
        data class Error(val message: String) : NotificationState()
    }
}