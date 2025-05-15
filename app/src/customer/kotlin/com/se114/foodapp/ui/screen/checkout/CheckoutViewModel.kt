package com.se114.foodapp.ui.screen.checkout

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.OrderItemRequest
import com.example.foodapp.data.dto.request.OrderRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.CheckoutUiModel
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.PaymentMethod
import com.example.foodapp.data.model.enums.ServingType
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.service.AccountService
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.data.repository.CartRepository



import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val accountService: AccountService,
private val foodApi: FoodApi

) : BaseViewModel() {


    private val _uiState = MutableStateFlow<ResultState>(ResultState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<CheckoutEvents>()
    val event = _event.receiveAsFlow()

    private val _cartItems = cartRepository.cartItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems
    private val _checkoutDetails = cartRepository.checkoutDetailsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CheckoutDetails(BigDecimal(0), BigDecimal(0), BigDecimal(0), BigDecimal(0))
    )
    val checkoutDetails: StateFlow<CheckoutDetails> = _checkoutDetails

    private val _checkoutRequest = MutableStateFlow(CheckoutUiModel(
        foodTableId = null,
        voucher = null,
        method = PaymentMethod.CASH.display,
        type = ServingType.ONLINE.display,
        note = "",
        address = null,
    ))
    val checkoutRequest= _checkoutRequest.asStateFlow()


    fun onFoodTableIdChanged(id: Int?) {
        _checkoutRequest.update { it.copy(foodTableId = id) }
    }

    fun onVoucherChanged(voucher: Voucher?) {
        _checkoutRequest.update { it.copy(voucher = voucher) }
    }


    fun onServingTypeChanged(type: String) {
        _checkoutRequest.update { it.copy(type = type) }
    }

    fun onPaymentMethodChanged(method: String) {
        _checkoutRequest.update { it.copy(method = method) }
    }
    fun onNoteChanged(note: String){
        _checkoutRequest.update { it.copy(note= note) }
    }

    fun onAddressChanged(address: String){
        _checkoutRequest.update { it.copy(address = address) }
    }



    fun onAddressClicked() {
        viewModelScope.launch {
            _event.send(CheckoutEvents.OnAddress)
        }

    }

    fun onBackClicked() {
        viewModelScope.launch {
            _event.send(CheckoutEvents.OnBack)
        }
    }

    fun onVoucherClicked(){
        viewModelScope.launch {
            _event.send(CheckoutEvents.OnCustomerVoucher)
        }
    }

    fun onCustomerConfirmClicked() {
        viewModelScope.launch {
            _uiState.value = ResultState.Loading

            val customerId = accountService.currentUserId

            val request = OrderRequest(
                customerId = customerId,
                foodTableId = _checkoutRequest.value.foodTableId,
                voucherId = _checkoutRequest.value.voucher?.id,
                type = _checkoutRequest.value.type,
                method = _checkoutRequest.value.method,
                startAt = StringUtils.getCurrentVietnamLocalTime(),
                paymentAt = StringUtils.getCurrentVietnamLocalTime(),
                note = _checkoutRequest.value.note,
                address = _checkoutRequest.value.address,
                orderItems = _cartItems.value.map { cartItem ->
                    OrderItemRequest(
                        foodId = cartItem.id,
                        quantity = cartItem.quantity,
                    ) }
            )


            delay(3000)
            Log.d("PaymentMethod", request.method)
            Log.d("Order Items", "${_cartItems.value.size}")
            try {
                val response = safeApiCall { foodApi.createOrder(request) }
                when (response) {
                    is ApiResponse.Success -> {
                        val orderId = response.body?.id
                        _uiState.value = ResultState.Success
                        _event.send(CheckoutEvents.OrderSuccess(orderId))
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
        data object OnAddress : CheckoutEvents()
        data object OnCustomerVoucher: CheckoutEvents()
        data object OnBack: CheckoutEvents()
        data class OrderSuccess(val orderId: Long?) : CheckoutEvents()

    }
}


