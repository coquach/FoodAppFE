package com.se114.foodapp.ui.screen.order

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.repository.OrderRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class OrderListViewModel
@Inject constructor(
    private val orderRepository: OrderRepository,
) : BaseViewModel() {

    private val _state = MutableStateFlow<OrderListState>(OrderListState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = Channel<OrderListEvent>()
    val event get() = _event.receiveAsFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex

    fun setTab(index: Int) {
        _tabIndex.value = index
    }
    fun refreshAllTabs() {
        ordersCache.clear()

    }
    private val ordersCache = mutableMapOf<Int, StateFlow<PagingData<Order>>>()


    fun getOrdersByTab(index: Int): StateFlow<PagingData<Order>> {
        return ordersCache.getOrPut(index) {
            val status = when (index) {
                0 -> OrderStatus.PENDING
                1 -> OrderStatus.CONFIRMED
                2 -> OrderStatus.DELIVERED
                3 -> OrderStatus.COMPLETED
                4 -> OrderStatus.CANCELLED
                else -> null
            }

            val filter = OrderFilter(status = status?.name)

            orderRepository.getOrdersByFilter(filter)
                .cachedIn(viewModelScope)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
                )
        }
    }
    fun navigateToDetails(it: Order) {
        viewModelScope.launch {
            _event.send(OrderListEvent.NavigateToOrderDetailScreen(it))
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