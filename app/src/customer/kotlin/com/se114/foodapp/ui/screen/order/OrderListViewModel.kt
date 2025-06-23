package com.se114.foodapp.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import com.example.foodapp.domain.use_case.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getOrdersUseCase: GetOrdersUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        OrderList.UiState(
            orderFilter = OrderFilter(
                status = OrderStatus.PENDING.name,
                customerId = getUserIdUseCase()
            )
        )
    )
    val uiState: StateFlow<OrderList.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<OrderList.Event>()
    val event get() = _event.receiveAsFlow()


    val orders = getOrdersUseCase.invoke(_uiState.value.orderFilter)


    fun onAction(action: OrderList.Action) {
        when (action) {
            is OrderList.Action.OnOrderClicked -> {
                viewModelScope.launch {
                    _event.send(OrderList.Event.GoToDetails(action.order))
                }
            }

            is OrderList.Action.OnTabChanged -> {
                _uiState.update { it.copy(orderFilter = it.orderFilter.copy(status = action.status)) }
            }


        }

    }
}

object OrderList {
    data class UiState(
        val orderFilter: OrderFilter = OrderFilter(),
    )

    sealed interface Event {
        data class GoToDetails(val order: Order) : Event
    }

    sealed interface Action {
        data class OnTabChanged(val status: String?) : Action
        data class OnOrderClicked(val order: Order) : Action
    }
}