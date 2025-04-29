package com.se114.foodapp.ui.screen.checkout

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderItemRequest
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.data.model.enums.ServingType
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.data.repository.CartRepository



import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
private val foodApi: FoodApi

) : BaseViewModel() {


    private val _uiState = MutableStateFlow<ResultState>(ResultState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CheckoutEvents>()
    val event = _event.asSharedFlow()

    private val _cartItems = cartRepository.cartItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems
    private val _checkoutDetails = cartRepository.checkoutDetailsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CheckoutDetails(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0))
    )
    val checkoutDetails: StateFlow<CheckoutDetails> = _checkoutDetails

    private val _orderRequest = MutableStateFlow(OrderRequest(
        customerId = "11",
        foodTableId = null,
        voucherId = null,
        staffId = null,
        servingType = ServingType.ONLINE.toString(),
        paymentMethod = PaymentMethod.CASH.display,
        orderDate = "",
        createAt = "",
        paymentAt = "",
        note = "",
        address = null,
        orderItems = emptyList(),
    ))
    val orderRequest= _orderRequest.asStateFlow()

    // Update methods (change functions)
    fun onCustomerIdChanged(id: String) {
        _orderRequest.update { it.copy(customerId = id) }
    }

    fun onFoodTableIdChanged(id: Long?) {
        _orderRequest.update { it.copy(foodTableId = id) }
    }

    fun onVoucherIdChanged(id: Long?) {
        _orderRequest.update { it.copy(voucherId = id) }
    }

    fun onStaffIdChanged(id: Long?) {
        _orderRequest.update { it.copy(staffId = id) }
    }

    fun onServingTypeChanged(type: String) {
        _orderRequest.update { it.copy(servingType = type) }
    }

    fun onPaymentMethodChanged(method: String) {
        _orderRequest.update { it.copy(paymentMethod = method) }
    }
    fun onNoteChanged(note: String){
        _orderRequest.update { it.copy(note= note) }
    }



    fun onAddressClicked() {
        viewModelScope.launch {
            _event.emit(CheckoutEvents.OnAddress)
        }

    }

    fun onConfirmClicked() {
        viewModelScope.launch {
            _uiState.value = ResultState.Loading

            _orderRequest.update { it.copy(
                orderDate = StringUtils.getFormattedCurrentVietnamDate(),
                createAt = StringUtils.getCurrentVietnamLocalTime(),
                paymentAt = StringUtils.getCurrentVietnamLocalTime(),
                paymentMethod = PaymentMethod.fromDisplay(it.paymentMethod)!!.name,
                orderItems = _cartItems.value.map { cartItem ->
                    OrderItemRequest(
                        menuItemId = cartItem.id,
                        quantity = cartItem.quantity,
                    ) }
            ) }
            delay(3000)
            Log.d("PaymentMethod", _orderRequest.value.paymentMethod)
            Log.d("Order Items", "${_cartItems.value.size}")
            try {
                val response = safeApiCall { foodApi.createOrder(_orderRequest.value) }
                when (response) {
                    is ApiResponse.Success -> {
                        val orderId = response.body?.id
                        _uiState.value = ResultState.Success
                        _event.emit(CheckoutEvents.OrderSuccess(orderId))
                        cartRepository.clearAll()
                    }
                     is ApiResponse.Error -> {
                         _uiState.value = ResultState.Error(response.message)
                     }
                    else -> {
                        _uiState.value = ResultState.Error("Lỗi không xác định")
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
                _uiState.value = ResultState.Error("Lỗi không xác định: ${e.message}")
            }

        }
    }




    sealed class CheckoutEvents {
        data object ShowErrorDialog : CheckoutEvents()
        data object OnCheckOut : CheckoutEvents()
        data object OnAddress : CheckoutEvents()
        data class OrderSuccess(val orderId: Long?) : CheckoutEvents()

    }
}

