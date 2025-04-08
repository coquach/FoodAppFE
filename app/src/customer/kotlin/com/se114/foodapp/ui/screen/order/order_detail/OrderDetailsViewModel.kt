package com.example.foodapp.ui.screen.order.order_detail

import androidx.lifecycle.ViewModel
import com.example.foodapp.R
import com.example.foodapp.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class OrderDetailsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Loading)
    val state get() = _state.asStateFlow()


    fun getOrderDetail(orderId: String) {

    }

    fun getImage(order: Order): Int {
        when (order.status) {
            "Đang giao" -> return R.drawable.ic_delivered
            "Đang chuẩn bị" -> return R.drawable.ic_preparing
            "Đang trên đường giao" -> return R.drawable.ic_on_way
            "Đã hủy" -> return R.drawable.ic_cancel
            else -> return R.drawable.ic_pending
        }
    }

    sealed class OrderDetailsState {
        data object Loading : OrderDetailsState()
        data class OrderDetails(val order: Order) : OrderDetailsState()
        data class Error(val message: String) : OrderDetailsState()
    }
}