package com.example.foodapp.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.repository.OrderRepoImpl
import com.example.foodapp.domain.use_case.auth.GetCustomerIdUseCase
import com.example.foodapp.domain.use_case.order.GetOrdersByCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val getOrdersByCustomerUseCase: GetOrdersByCustomerUseCase
) : ViewModel() {


    private val _event = Channel<OrderList.Event>()
    val event get() = _event.receiveAsFlow()
    fun refreshAllTabs() {
        ordersCache.clear()

    }
    private val ordersCache = mutableMapOf<Int, StateFlow<PagingData<Order>>>()


    fun getOrdersByTab(index: Int): StateFlow<PagingData<Order>> {
        return ordersCache.getOrPut(index) {
            val status = when (index) {
                0 -> OrderStatus.PENDING
                1 -> null
                else -> null
            }

            val filter = OrderFilter(status = status?.name)

            getOrdersByCustomerUseCase.invoke(filter)
                .cachedIn(viewModelScope)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
                )
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
                getOrdersByTab(action.index)
            }

        }

    }
}

object OrderList{
    sealed interface Event {
        data class GoToDetails(val order: Order) : Event
    }
    sealed interface Action{
        data class OnTabChanged(val index: Int) : Action
        data class OnOrderClicked(val order: Order) : Action

    }
}