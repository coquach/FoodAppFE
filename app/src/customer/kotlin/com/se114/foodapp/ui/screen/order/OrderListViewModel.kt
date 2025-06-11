package com.se114.foodapp.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import com.example.foodapp.domain.use_case.order.GetOrdersUseCase
import com.example.foodapp.utils.TabCacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val getUserIdUseCase: GetUserIdUseCase,
    private val getOrdersUseCase: GetOrdersUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderList.UiState(
        orderFilter = OrderFilter(status = OrderStatus.PENDING.name, customerId = getUserIdUseCase())
    ))
    val uiState: StateFlow<OrderList.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<OrderList.Event>()
    val event get() = _event.receiveAsFlow()

    val ordersTabManager = TabCacheManager<Int, Order>(
        scope = viewModelScope,
        getFilter = { tabIndex ->
            val status = getOrderStatusForTab(tabIndex)
             _uiState.value.orderFilter.copy(status = status?.name)
        },
        loadData = { filter ->
            getOrdersUseCase(filter as OrderFilter)
        }
    )

    fun getOrdersFlow(tabIndex: Int){
        return ordersTabManager.getFlowForTab(tabIndex)
    }




    private fun getOrderStatusForTab(tabIndex: Int): OrderStatus? {
        return when (tabIndex) {
            0 -> OrderStatus.PENDING
            else -> null
        }
    }
    fun onAction(action: OrderList.Action) {
        when (action) {
            is OrderList.Action.OnOrderClicked -> {
                viewModelScope.launch {
                    _event.send(OrderList.Event.GoToDetails(action.order))
                }
            }

            is OrderList.Action.OnTabChanged -> {
                _uiState.update { it.copy(tabIndex = action.index) }
            }
            OrderList.Action.OnRefresh -> {
                ordersTabManager.refreshAllTabs()
                getOrdersFlow(_uiState.value.tabIndex)
            }


        }

    }
}

object OrderList {
    data class UiState(
        val tabIndex: Int = 0,
        val orderFilter: OrderFilter = OrderFilter(),
    )

    sealed interface Event {
        data class GoToDetails(val order: Order) : Event
    }

    sealed interface Action {
        data class OnTabChanged(val index: Int) : Action
        data class OnOrderClicked(val order: Order) : Action
        data object OnRefresh : Action

    }
}