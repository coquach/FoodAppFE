package com.se114.foodapp.ui.screen.checkout

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderItemRequest
import com.example.foodapp.data.model.CheckoutDetails

import com.example.foodapp.data.model.CheckoutUiModel
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.OrderItem
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.data.model.enums.ServingType
import com.example.foodapp.data.model.toCheckoutUiModel
import com.example.foodapp.navigation.CheckoutStaff
import com.se114.foodapp.domain.use_case.order.CancelOrderUseCase

import com.se114.foodapp.domain.use_case.order.CheckoutOrderUseCase
import com.se114.foodapp.domain.use_case.order.GetOrderByFoodTableIdUseCase
import com.se114.foodapp.domain.use_case.order.UpsertOrderItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(

    saveStateHandle: SavedStateHandle,
    private val upsertOrderItemsUseCase: UpsertOrderItemsUseCase,
    private val getOrderByFoodTableIdUseCase: GetOrderByFoodTableIdUseCase,
    private val checkoutOrderUseCase: CheckoutOrderUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,


    ) : ViewModel() {
    private val foodTableId = saveStateHandle.toRoute<CheckoutStaff>().foodTableID
    private val _uiState = MutableStateFlow(Checkout.UiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<Checkout.Event>()
    val event = _event.receiveAsFlow()

    init{
        getOrderByFoodTableId()
    }

    private fun getOrderByFoodTableId() {
        viewModelScope.launch {
            getOrderByFoodTableIdUseCase(foodTableId).collect { result ->
                when (result) {
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(getOrdersState = Checkout.GetOrderState.Loading) }
                    }

                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                getOrdersState = Checkout.GetOrderState.Success,
                                checkout = result.data.toCheckoutUiModel(),
                                orderItems = result.data.orderItems,
                            )
                        }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                getOrdersState = Checkout.GetOrderState.Error(result.errorMessage),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkoutOrder() {
        viewModelScope.launch {
            checkoutOrderUseCase(
                orderId = _uiState.value.checkout.id!!,
                voucherId = _uiState.value.checkout.voucher?.id
            ).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                            )
                        }
                        _event.send(Checkout.Event.OrderSuccess(_uiState.value.checkout.id!!))
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.errorMessage
                            )
                        }
                        _event.send(Checkout.Event.ShowError)
                    }
                }
            }
        }
    }

    private fun cancelOrder() {
        viewModelScope.launch {
            cancelOrderUseCase(
                orderId = _uiState.value.checkout.id!!,
            ).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                            )

                        }
                        _event.send(Checkout.Event.ShowToastSuccess("Hủy đơn hàng thành công"))
                        _event.send(Checkout.Event.OnBack)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.errorMessage
                            )

                        }
                        _event.send(Checkout.Event.ShowError)
                    }
                }
            }
        }
    }

    private fun upsertOrderItems() {
        val orderItems = _uiState.value.orderItems.map {
            OrderItemRequest(
                foodId = it.foodId!!,
                quantity = it.quantity,
            )
        }
        Log.d("CheckoutViewModel", "upsertOrderItems: $orderItems")
        viewModelScope.launch {
            upsertOrderItemsUseCase(
                orderId = _uiState.value.checkout.id!!,
                orderItems = _uiState.value.orderItems.map {
                    OrderItemRequest(
                        foodId = it.foodId!!,
                        quantity = it.quantity,
                    )
                },

            ).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                            )
                        }
                        _event.send(Checkout.Event.ShowToastSuccess("Cập nhật chi tiết đơn hàng thành công"))
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.errorMessage
                            )
                        }
                        _event.send(Checkout.Event.ShowError)
                    }
                }
            }
        }
    }

    private fun removeOrderItems(id: Long){
        _uiState.update { it.copy(orderItems = _uiState.value.orderItems.filter { it.id != id }) }
    }

    private fun updateQuantity(id: Long, quantity: Int){
        _uiState.update { it.copy(orderItems = _uiState.value.orderItems.map {
            if (it.id == id) it.copy(
                quantity = quantity
            ) else it
        }) }
    }

    fun onAction(action: Checkout.Action) {
        when (action) {


            is Checkout.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(Checkout.Event.OnBack)
                }
            }


            is Checkout.Action.OnChooseVoucher -> {
                viewModelScope.launch {
                    _event.send(Checkout.Event.ChooseVoucher)
                }
            }

            is Checkout.Action.OnVoucherChanged -> {
                _uiState.update { it.copy(checkout = _uiState.value.checkout.copy(voucher = action.voucher)) }
            }

            Checkout.Action.CheckoutOrder -> {
                checkoutOrder()
            }

            Checkout.Action.CancelOrder -> {
                cancelOrder()
            }

            Checkout.Action.SaveOrderItems -> {
                upsertOrderItems()
            }

            is Checkout.Action.OnOrderItemsChanged -> {
                _uiState.update { it.copy(orderItems = action.orderItems) }
            }
            is Checkout.Action.OnQuantityChange -> {
                if(action.quantity == 0) removeOrderItems(action.id)
                else updateQuantity(action.id, action.quantity)

            }
            Checkout.Action.NavigateToGetFood -> {
                viewModelScope.launch {
                    _event.send(Checkout.Event.NavigateToGetFood)
                }
            }

            is Checkout.Action.RemoveOrderItem -> {
                removeOrderItems(action.id)
            }
        }
    }

}

object Checkout {
    data class UiState(
        val isLoading: Boolean = false,
        val getOrdersState: GetOrderState = GetOrderState.Loading,
        val orderItems: List<OrderItem> = emptyList(),
        val error: String? = null,
        val checkout: CheckoutUiModel = CheckoutUiModel(
            foodTableNumber = null,
            voucher = null,
            method = PaymentMethod.CASH.display,
            type = ServingType.INSTORE.display,
            status = OrderStatus.CONFIRMED.name,
            note = "",
            address = null,
        ),
    )

    sealed interface Event {
        data class ShowToastSuccess(val message: String) : Event
        data object ShowError : Event
        data object ChooseVoucher : Event
        data object OnBack : Event
        data class OrderSuccess(val orderId: Long) : Event
        data object NavigateToGetFood: Event
    }

    sealed interface GetOrderState {
        data object Loading : GetOrderState
        data object Success : GetOrderState
        data class Error(val message: String) : GetOrderState
    }

    sealed interface Action {
        data object OnChooseVoucher : Action
        data object OnBack : Action
        data object CheckoutOrder : Action
        data object CancelOrder : Action
        data class OnOrderItemsChanged(val orderItems: List<OrderItem>) : Action
        data object SaveOrderItems : Action
        data class OnVoucherChanged(val voucher: Voucher) : Action
        data class OnQuantityChange(val id: Long, val quantity: Int) : Action
        data class RemoveOrderItem(val id: Long) : Action
        data object NavigateToGetFood: Action

    }
}

