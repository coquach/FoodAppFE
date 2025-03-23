package com.example.foodapp.ui.screen.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.OrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<OrderListState>(OrderListState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<OrderListEvent>()
    val event get() = _event.asSharedFlow()

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            delay(1000) // Giả lập thời gian tải dữ liệu
//            val  sampleOrders = emptyList<Order>()
            val sampleOrders = listOf(
                Order(
                    id = "1",
                    userId = "user_123",
                    createdAt = "2025-03-23T10:00:00Z",
                    updatedAt = "2025-03-23T10:30:00Z",
                    status = "Đang chờ",
                    totalAmount = 150.0f,
                    paymentMethod = "Credit Card",
                    address = Address(
                        addressLine1 = "123 Main St",
                        city = "New York",
                        state = "NY",
                        zipCode = "10001",
                        country = "USA"
                    ),
                    items = listOf(
                        OrderItem(
                            id = "item_1",
                            menuItemId = "menu_123",
                            orderId = "1",
                            quantity = 2,
                            menuItemName = "Espresso"
                        )
                    )
                ),
                Order(
                    id = "2",
                    userId = "user_456",
                    createdAt = "2025-03-23T11:00:00Z",
                    updatedAt = "2025-03-23T11:15:00Z",
                    status = "Đã gửi",
                    totalAmount = 250.0f,
                    paymentMethod = "PayPal",
                    address = Address(
                        addressLine1 = "456 Elm St",
                        city = "Los Angeles",
                        state = "CA",
                        zipCode = "90001",
                        country = "USA"
                    ),
                    items = listOf(
                        OrderItem(
                            id = "item_2",
                            menuItemId = "menu_456",
                            orderId = "2",
                            quantity = 1,
                            menuItemName = "Cappuccino"
                        )
                    )
                ),
                Order(
                    id = "2",
                    userId = "user_456",
                    createdAt = "2025-03-23T11:00:00Z",
                    updatedAt = "2025-03-23T11:15:00Z",
                    status = "Đã hủy",
                    totalAmount = 250.0f,
                    paymentMethod = "PayPal",
                    address = Address(
                        addressLine1 = "456 Elm St",
                        city = "Los Angeles",
                        state = "CA",
                        zipCode = "90001",
                        country = "USA"
                    ),
                    items = listOf(
                        OrderItem(
                            id = "item_2",
                            menuItemId = "menu_456",
                            orderId = "2",
                            quantity = 1,
                            menuItemName = "Cappuccino"
                        )
                    )
                )
            )

            _state.value = OrderListState.Success(sampleOrders)
        }
    }


    fun navigateToDetails(order: Order) {
        viewModelScope.launch {
            _event.emit(OrderListEvent.NavigateToOrderDetailScreen(order))
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