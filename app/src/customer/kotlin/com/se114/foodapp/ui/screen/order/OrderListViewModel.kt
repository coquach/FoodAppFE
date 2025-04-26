package com.example.foodapp.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow<OrderListState>(OrderListState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<OrderListEvent>()
    val event get() = _event.asSharedFlow()

    private val _ordersPending = MutableStateFlow<PagingData<Order>>(PagingData.empty())
    val ordersPending = _ordersPending
    private val _ordersAll = MutableStateFlow<PagingData<Order>>(PagingData.empty())
    val ordersAll= _ordersAll

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {


            orderRepository.getOrdersByFilter(OrderFilter(status = OrderStatus.PENDING.name))
                .cachedIn(viewModelScope).collect {
                _ordersPending.value = it
            }
        }
        viewModelScope.launch {
            orderRepository.getOrdersByFilter(OrderFilter()).cachedIn(viewModelScope).collect{
                _ordersAll.value = it
            }

        }

    }


    fun navigateToDetails(it: Order) {
        viewModelScope.launch {
            _event.emit(OrderListEvent.NavigateToOrderDetailScreen(it))
        }
    }

    sealed class OrderListEvent {
        data class NavigateToOrderDetailScreen(val order: Order) : OrderListEvent()
        data object NavigateBack : OrderListEvent()
    }

    sealed class OrderListState {
        data object Loading : OrderListState()
        data object Success : OrderListState()
        data class Error(val message: String) : OrderListState()
    }
}