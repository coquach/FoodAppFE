package com.se114.foodapp.ui.screen.order

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.repository.OrderRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class OrderListViewModel
@Inject constructor(
    private val orderRepository: OrderRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow<OrderListState>(OrderListState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<OrderListEvent>()
    val event get() = _event.asSharedFlow()

    private val _ordersPending = MutableStateFlow<PagingData<Order>>(PagingData.empty())
    val ordersPending = _ordersPending
    private val _ordersConfirmed = MutableStateFlow<PagingData<Order>>(PagingData.empty())
    val ordersConfirmed= _ordersConfirmed
    private val _ordersDelivered = MutableStateFlow<PagingData<Order>>(PagingData.empty())
    val ordersDelivered= _ordersDelivered
    private val _ordersCompleted = MutableStateFlow<PagingData<Order>>(PagingData.empty())
    val ordersCompleted= _ordersCompleted
    private val _ordersCancelled = MutableStateFlow<PagingData<Order>>(PagingData.empty())
    val ordersCancelled= _ordersCancelled

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
            orderRepository.getOrdersByFilter(OrderFilter(OrderStatus.CONFIRMED.name)).cachedIn(viewModelScope).collect{
                _ordersConfirmed.value = it
            }

        }
        viewModelScope.launch {
            orderRepository.getOrdersByFilter(OrderFilter(OrderStatus.DELIVERED.name)).cachedIn(viewModelScope).collect{
                _ordersDelivered.value = it
            }

        }
        viewModelScope.launch {
            orderRepository.getOrdersByFilter(OrderFilter(OrderStatus.COMPLETED.name)).cachedIn(viewModelScope).collect{
                _ordersCompleted.value = it
            }

        }
        viewModelScope.launch {
            orderRepository.getOrdersByFilter(OrderFilter(OrderStatus.CANCELLED.name)).cachedIn(viewModelScope).collect{
                _ordersCancelled.value = it
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
        data class Success(val orderList: List<Order>) : OrderListState()
        data class Error(val message: String) : OrderListState()
    }
}