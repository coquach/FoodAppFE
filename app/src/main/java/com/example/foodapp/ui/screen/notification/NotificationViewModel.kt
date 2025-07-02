package com.example.foodapp.ui.screen.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.NotificationFilter
import com.example.foodapp.data.model.Notification
import com.example.foodapp.data.model.Order
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import com.example.foodapp.domain.use_case.notification.GetNotificationsUseCase
import com.example.foodapp.domain.use_case.notification.ReadAllNotificationUseCase
import com.example.foodapp.domain.use_case.notification.ReadNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val readNotificationUseCase: ReadNotificationUseCase,
    private val readAllNotificationUseCase: ReadAllNotificationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState.UiState())
    val state get() = _state.asStateFlow()

    private val _event = Channel<NotificationState.Event>()
    val event get() = _event.receiveAsFlow()
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    init{
        getNotifications()
    }

    fun getNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase.invoke().collect { response ->
                when (response) {
                    is com.example.foodapp.data.dto.ApiResponse.Success -> {
                        _state.update {
                            it.copy(
                                notificationListState = NotificationState.NotificationListState.Success,
                                notifications = response.data
                            )
                        }
                        _unreadCount.value = response.data.count { !it.read }
                        Log.d("unRead", "${_unreadCount.value}")
                    }

                    is com.example.foodapp.data.dto.ApiResponse.Failure -> {
                        _state.update {
                            it.copy(
                                notificationListState = NotificationState.NotificationListState.Error(
                                    response.errorMessage
                                )
                            )
                        }

                    }

                    is com.example.foodapp.data.dto.ApiResponse.Loading -> {
                        _state.update { it.copy(notificationListState = NotificationState.NotificationListState.Loading) }
                    }
                }
            }
        }
    }

    private fun readAllNotification() {
        viewModelScope.launch {
            readAllNotificationUseCase.invoke().collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {}
                    is ApiResponse.Success -> {
                        getNotifications()
                    }

                    is ApiResponse.Failure -> {
                        _state.update { it.copy(error = response.errorMessage) }
                    }
                }
            }
        }
    }


    private fun readNotification(id: Long) {
        viewModelScope.launch {
            readNotificationUseCase.invoke(id).collect { response ->
                when (response) {
                    is com.example.foodapp.data.dto.ApiResponse.Success -> {
                        getNotifications()
                    }

                    is com.example.foodapp.data.dto.ApiResponse.Failure -> {
                        _state.update { it.copy(error = response.errorMessage) }
                        _event.send(NotificationState.Event.ShowError)
                    }

                    is com.example.foodapp.data.dto.ApiResponse.Loading -> {}

                }
            }

        }
    }

    fun onAction(action: NotificationState.Action) {
        when (action) {
            is NotificationState.Action.OnNotificationClicked -> {
                _state.update { it.copy(selectedNotification = action.notification) }
                readNotification(action.notification.id)
            }

            is NotificationState.Action.Retry -> {
                getNotifications()
            }
            is NotificationState.Action.ReadAllNotification -> {
                readAllNotification()
            }

        }
    }


}

object NotificationState {
    data class UiState(
        val notifications: List<Notification> = emptyList(),
        val notificationListState: NotificationListState = NotificationListState.Loading,
        val selectedNotification: Notification? = null,
        val error: String? = null,
    )

    sealed interface NotificationListState {
        data object Loading : NotificationListState
        data object Success : NotificationListState
        data class Error(val message: String) : NotificationListState

    }

    sealed interface Event {
        data object ShowError : Event
    }

    sealed interface Action {
        data object ReadAllNotification : Action
        data class OnNotificationClicked(val notification: Notification) : Action
        data object Retry : Action
    }
}