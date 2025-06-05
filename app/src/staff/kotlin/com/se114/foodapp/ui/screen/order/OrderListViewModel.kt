package com.se114.foodapp.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.domain.use_case.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
@OptIn(ExperimentalCoroutinesApi::class)
class OrderListViewModel
@Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderListState.UiState())
    val state get() = _state.asStateFlow()

    private val _event = Channel<OrderListState.Event>()
    val event get() = _event.receiveAsFlow()




    private fun refreshAllTabs() {
        ordersCache.clear()

    }
    private val ordersCache = mutableMapOf<Int, StateFlow<PagingData<Order>>>()


    fun getOrdersByTab(index: Int): StateFlow<PagingData<Order>> {
        return ordersCache.getOrPut(index) {
            val status = when (index) {
                0 -> OrderStatus.PENDING
                1 -> OrderStatus.CONFIRMED
                2 -> OrderStatus.READY
                3 -> OrderStatus.SHIPPING
                4 -> OrderStatus.COMPLETED
                5 -> OrderStatus.CANCELLED
                else -> null
            }

            val filter = OrderFilter(status = status?.name)

            getOrdersUseCase.invoke(filter)
                .cachedIn(viewModelScope)
                .stateIn(
                    viewModelScope,
                    SharingStarted.Lazily ,
                    PagingData.empty()
                )
        }
    }

    fun onAction(action: OrderListState.Action){
        when(action){
            is OrderListState.Action.OnTabSelected -> {
                _state.update { it.copy(tabIndex = action.index) }
            }
            is OrderListState.Action.OnOrderClicked -> {
                viewModelScope.launch {
                    _event.send(OrderListState.Event.GoToDetail(action.order))
                }
            }
            OrderListState.Action.OnRefresh -> {
                refreshAllTabs()
            }
        }
    }



}

object OrderListState{
    data class UiState(
        val tabIndex: Int = 0,
    )
    sealed interface Event{
        data class GoToDetail(val order: Order): Event
        data object Refresh: Event
    }

    sealed interface Action{
        data class OnTabSelected(val index: Int): Action
        data class OnOrderClicked(val order: Order): Action
        data object OnRefresh: Action

    }

}