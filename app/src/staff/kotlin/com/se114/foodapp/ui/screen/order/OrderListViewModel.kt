package com.se114.foodapp.ui.screen.order

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.mapper.OrderMapper.toOrder
import com.example.foodapp.ui.screen.auth.BaseViewModel
import com.se114.foodapp.data.repository.OrderRepository
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


    private fun getOrdersByStatus(status: OrderStatus) = orderRepository
        .getOrdersByFilter(OrderFilter(status = status.toString()))
        .map { pagingData -> pagingData.map { it.toOrder() } }
        .cachedIn(viewModelScope)

    val pendingOrders = getOrdersByStatus(OrderStatus.PENDING)
    val confirmedOrders = getOrdersByStatus(OrderStatus.CONFIRMED)
    val deliveredOrders = getOrdersByStatus(OrderStatus.DELIVERED)
    val completedOrders = getOrdersByStatus(OrderStatus.COMPLETED)
    val cancelledOrders = getOrdersByStatus(OrderStatus.CANCELLED)

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