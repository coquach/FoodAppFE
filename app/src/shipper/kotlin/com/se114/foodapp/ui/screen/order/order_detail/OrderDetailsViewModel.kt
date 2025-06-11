package com.se114.foodapp.ui.screen.order.order_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderStatusRequest
import com.example.foodapp.data.model.Order
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import com.example.foodapp.domain.use_case.order.UpdateStatusOrderUseCase
import com.example.foodapp.navigation.OrderDetailsStaff
import com.example.foodapp.navigation.orderNavType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf


@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val updateStatusOrderUseCase: UpdateStatusOrderUseCase,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val navArgs = savedStateHandle.toRoute<OrderDetailsStaff>(
        typeMap = mapOf(typeOf<Order>() to orderNavType)
    )
    private val orderLoad = navArgs.order


    private val _state = MutableStateFlow(OrderDetailsState.UiState(order = orderLoad))
    val state : StateFlow<OrderDetailsState.UiState> get() = _state.asStateFlow()

    private val _event = Channel<OrderDetailsState.Event>()
    val events = _event.receiveAsFlow()

    private fun updateStatusOrder(status: String) {
        viewModelScope.launch {

          updateStatusOrderUseCase.invoke(_state.value.order.id,
              OrderStatusRequest(
                  status = status,
                  shipperId = getUserIdUseCase()
              )).collect{ response ->
              when(response) {
                  is ApiResponse.Loading -> {
                      _state.update { it.copy(isLoading = true) }
                  }
                  is ApiResponse.Success -> {
                      _state.update { it.copy(isLoading = false) }
                      _event.send(OrderDetailsState.Event.UpdateOrder)
                  }
                  is ApiResponse.Failure -> {
                      _state.update { it.copy(isLoading = false, error = response.errorMessage) }
                  }
              }
          }
        }
    }
    fun onAction(action: OrderDetailsState.Action) {
        when (action) {
            is OrderDetailsState.Action.UpdateStatusOrder -> {
                updateStatusOrder(action.status)
            }
            is OrderDetailsState.Action.OnChangeCanUpdateStatus -> {
                _state.update { it.copy(canUpdateStatus = action.canUpdateStatus) }
            }
            is OrderDetailsState.Action.GoToTracking -> {
                viewModelScope.launch {
                    _event.send(OrderDetailsState.Event.GoToTracking(action.lon, action.lat))
                }
            }
            is OrderDetailsState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(OrderDetailsState.Event.OnBack)
                }
            }
        }
    }

}

object OrderDetailsState{
    data class UiState(

        val order: Order,
        val isLoading: Boolean = false,
        val error: String? = null,
        val canUpdateStatus: Boolean = false,
    )
    sealed interface Event {
        data object ShowError : Event
        data object UpdateOrder : Event
        data class GoToTracking(val lon: Double, val lat: Double) : Event
        data object OnBack : Event

    }

    sealed interface Action{
        data class UpdateStatusOrder(val status: String) : Action
        data class OnChangeCanUpdateStatus(val canUpdateStatus: Boolean) : Action
        data class GoToTracking(val lon: Double, val lat: Double) : Action
        data object OnBack : Action
    }
}