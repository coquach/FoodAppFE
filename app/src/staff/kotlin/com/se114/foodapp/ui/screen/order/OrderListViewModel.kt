package com.se114.foodapp.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.domain.use_case.order.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    fun getOrders(filter: OrderFilter) = getOrdersUseCase(filter)


    fun onAction(action: OrderListState.Action){
        when(action){

            is OrderListState.Action.OnOrderClicked -> {
                viewModelScope.launch {
                    _event.send(OrderListState.Event.GoToDetail(action.order))
                }
            }
            OrderListState.Action.OnRefresh -> {

            }
            is OrderListState.Action.OnDateChange -> {
                _state.update {
                    it.copy(
                        orderFilter = it.orderFilter.copy(
                            startDate = action.startDate,
                            endDate = action.endDate))}
            }
            is OrderListState.Action.OnStatusFilterChange -> {
                _state.update {
                    it.copy(
                        orderFilter = it.orderFilter.copy(
                            status = action.status))}
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
    )
    sealed interface Event{
        data class GoToDetail(val order: Order): Event
    }

    sealed interface Action{
        data class OnStatusFilterChange(val status: String): Action
        data class OnDateChange(val startDate: LocalDate?, val endDate: LocalDate?): Action
        data class OnOrderClicked(val order: Order): Action
        data object OnRefresh: Action

    }

}