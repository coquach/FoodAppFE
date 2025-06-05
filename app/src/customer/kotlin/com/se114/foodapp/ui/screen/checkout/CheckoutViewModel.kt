package com.se114.foodapp.ui.screen.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderItemRequest
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.data.model.CheckoutUiModel
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.data.model.enums.ServingType
import com.example.foodapp.domain.use_case.order.PlaceOrderUseCase
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.domain.use_case.cart.GetCartUseCase
import com.se114.foodapp.domain.use_case.cart.GetCheckOutDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val getCheckoutDetailsUseCase: GetCheckOutDetailsUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
) : ViewModel() {


    private val _uiState = MutableStateFlow(Checkout.UiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<Checkout.Event>()
    val event = _event.receiveAsFlow()

    init {
        getCartItems()
        getCheckoutDetails()
    }

    private fun getCartItems() {
        viewModelScope.launch {
            try {
                val cartItems = getCartUseCase.invoke().first()
                _uiState.update { it.copy(cartItems = cartItems) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Đã xảy ra lỗi khi lấy giỏ hàng") }
                _event.send(Checkout.Event.ShowError)
            }
        }
    }

    private fun getCheckoutDetails() {
        viewModelScope.launch {
            try {
                val checkoutDetails = getCheckoutDetailsUseCase.invoke().first()
                _uiState.update { it.copy(checkoutDetails = checkoutDetails) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Đã xảy ra lỗi khi lấy chi tiết giá đơn hàng"
                    )
                }
                _event.send(Checkout.Event.ShowError)
            }
        }
    }

    private fun placeOrder() {
        viewModelScope.launch {
            try {

                val request = OrderRequest(
                    foodTableId = _uiState.value.checkout.foodTableId,
                    voucherId = _uiState.value.checkout.voucher?.id,
                    type = _uiState.value.checkout.type,
                    method = _uiState.value.checkout.method,
                    startAt = StringUtils.getCurrentVietnamLocalTime(),
                    paymentAt = StringUtils.getCurrentVietnamLocalTime(),
                    note = _uiState.value.checkout.note,
                    address = _uiState.value.checkout.address,
                    orderItems = _uiState.value.cartItems.map { cartItem ->
                        OrderItemRequest(
                            foodId = cartItem.id,
                            quantity = cartItem.quantity,
                        )
                    }
                )

                placeOrderUseCase(request).collect { result ->
                    when (result) {
                        is ApiResponse.Success -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.send(Checkout.Event.OrderSuccess(result.data.id))
                        }

                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(
                                    error = result.errorMessage,
                                    isLoading = false
                                )
                            }
                            _event.send(Checkout.Event.ShowError)
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Đã xảy ra lỗi khi đặt hàng",
                        isLoading = false
                    )
                }
                _event.send(Checkout.Event.ShowError)

            }
        }
    }

    fun onAction(action: Checkout.Action) {
        when (action) {
            is Checkout.Action.OnChooseAddress -> {
                viewModelScope.launch {
                    _event.send(Checkout.Event.ChooseAddress)
                }
            }

            is Checkout.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(Checkout.Event.OnBack)
                }
            }

            is Checkout.Action.PlaceOrder -> {
                placeOrder()
            }

            is Checkout.Action.OnNoteChanged -> {
                _uiState.update { it.copy(checkout = _uiState.value.checkout.copy(note = action.note)) }
            }

            is Checkout.Action.OnPaymentMethodChanged -> {
                _uiState.update { it.copy(checkout = _uiState.value.checkout.copy(method = action.method)) }
            }

            is Checkout.Action.OnServingTypeChanged -> {
                _uiState.update { it.copy(checkout = _uiState.value.checkout.copy(type = action.type)) }
            }

            is Checkout.Action.OnFoodTableIdChanged -> {
                _uiState.update { it.copy(checkout = _uiState.value.checkout.copy(foodTableId = action.id)) }
            }

            is Checkout.Action.OnChooseVoucher -> {
                viewModelScope.launch {
                    _event.send(Checkout.Event.ChooseVoucher)
                }
            }

            is Checkout.Action.OnVoucherChanged -> {
                _uiState.update { it.copy(checkout = _uiState.value.checkout.copy(voucher = action.voucher)) }
            }

            is Checkout.Action.OnAddressChanged -> {
                _uiState.update { it.copy(checkout = _uiState.value.checkout.copy(address = action.address)) }
            }
        }
    }

}

object Checkout {
    data class UiState(
        val isLoading: Boolean = false,
        val cartItems: List<CartItem> = emptyList(),
        val checkoutDetails: CheckoutDetails = CheckoutDetails(
            BigDecimal(0),
            BigDecimal(0),
            BigDecimal(0),
            BigDecimal(0)
        ),
        val error: String? = null,
        val checkout: CheckoutUiModel = CheckoutUiModel(
            foodTableId = null,
            voucher = null,
            method = PaymentMethod.CASH.display,
            type = ServingType.ONLINE.display,
            note = "",
            address = null,
        ),

    )

    sealed interface Event {
        data object ShowError : Event
        data object ChooseAddress : Event
        data object ChooseVoucher : Event
        data object OnBack : Event
        data class OrderSuccess(val orderId: Long) : Event
    }

    sealed interface Action {
        data object OnChooseAddress : Action
        data object OnChooseVoucher : Action
        data object OnBack : Action
        data object PlaceOrder : Action
        data class OnNoteChanged(val note: String) : Action
        data class OnPaymentMethodChanged(val method: String) : Action
        data class OnServingTypeChanged(val type: String) : Action
        data class OnFoodTableIdChanged(val id: Int?) : Action
        data class OnVoucherChanged(val voucher: Voucher) : Action
        data class OnAddressChanged(val address: String) : Action
    }
}

