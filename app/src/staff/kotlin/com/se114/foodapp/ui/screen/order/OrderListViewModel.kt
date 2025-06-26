package com.se114.foodapp.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import com.example.foodapp.domain.use_case.order.GetOrdersUseCase
import com.example.foodapp.utils.TabCacheManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class OrderListViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrderListState.UiState(
        orderFilter = OrderFilter(status = OrderStatus.PENDING.name)
    ))
    val state get() = _state.asStateFlow()

    private val _event = Channel<OrderListState.Event>()
    val event get() = _event.receiveAsFlow()


    val ordersTabManager = TabCacheManager<Int, Order>(
        scope = viewModelScope,
        getFilter = { tabIndex ->
            val status = getOrderStatusForTab(tabIndex)
            _state.value.orderFilter.copy(status = status?.name)
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
            1 -> OrderStatus.CONFIRMED
            2 -> OrderStatus.READY
            3 -> OrderStatus.SHIPPING
            4 -> OrderStatus.COMPLETED
            5 -> OrderStatus.CANCELLED
            else -> null
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

                getOrdersFlow(_state.value.tabIndex)
            }
        }
    }



}
private data class TabData(
    val flow: MutableStateFlow<PagingData<Order>> = MutableStateFlow(PagingData.empty()),
    var loadJob: Job? = null,
)

object OrderListState{
    data class UiState(
        val orderFilter: OrderFilter = OrderFilter(),
        val tabIndex: Int = 0,
    )
    sealed interface Event{
        data class GoToDetail(val order: Order): Event
    }

    sealed interface Action{
        data class OnTabSelected(val index: Int): Action
        data class OnOrderClicked(val order: Order): Action
        data object OnRefresh: Action

    }

}