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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

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
                1 -> null
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
        data object Success : OrderListState()
        data class Error(val message: String) : OrderListState()
    }
}