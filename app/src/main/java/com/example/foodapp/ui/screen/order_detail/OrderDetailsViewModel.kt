package com.example.foodapp.ui.screen.order.order_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.R
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.remote.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Delayed
import javax.inject.Inject


@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {

    private val _state = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Nothing)
    val state get() = _state.asStateFlow()

    private val _event = Channel<OrderDetailEvents>()
    val events = _event.receiveAsFlow()

    fun updateStatusOrder(orderId: Long, status: String) {
        viewModelScope.launch {
            _state.value = OrderDetailsState.Loading
            delay(3000)
            try {
                val response =
                    safeApiCall { foodApi.updateOrderStatus(orderId, status) }
                when(response) {
                    is ApiResponse.Success -> {
                        _state.value = OrderDetailsState.Success
                        _event.send(OrderDetailEvents.UpdateOrder)
                    }
                    is ApiResponse.Error -> {
                        _state.value = OrderDetailsState.Error(response.message)
                    }
                    else -> {
                        _state.value = OrderDetailsState.Error("Lỗi không xác định")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = OrderDetailsState.Error(e.message.toString())
            }
        }
    }

    sealed class OrderDetailsState {
        data object Nothing : OrderDetailsState()
        data object Loading : OrderDetailsState()
        data object Success :OrderDetailsState()
        data class Error(val message: String) : OrderDetailsState()
    }

    sealed class OrderDetailEvents {
        data object UpdateOrder : OrderDetailEvents()
    }
}