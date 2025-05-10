package com.se114.foodapp.ui.screen.checkout.voucher_check

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.repository.VoucherRepository
import com.example.foodapp.data.service.AccountService
import com.se114.foodapp.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class VoucherCheckViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val accountService: AccountService,
    private val voucherRepository: VoucherRepository,
    private val cartRepository: CartRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<VoucherCheckState>(VoucherCheckState.Nothing)
    val uiState = _uiState.asStateFlow()


    private val _event = Channel<VoucherCheckEvents>()
    val event = _event.receiveAsFlow()


    private val _vouchers = MutableStateFlow<PagingData<Voucher>>(PagingData.empty())
    val vouchers: StateFlow<PagingData<Voucher>>  = _vouchers





    private var isCustomer by mutableStateOf(false)

    fun setMode(mode: Boolean) {
        isCustomer = mode
        getVouchers()

    }

    private fun getVouchers() {
        viewModelScope.launch {
            delay(100)
            val checkout = cartRepository.checkoutDetailsFlow.first()
            val totalAmount = checkout.subTotal

            val flow = if (isCustomer) {
                val customerId = accountService.currentUserId
                voucherRepository.getVouchersByCustomerId(customerId!!)
            } else {
                voucherRepository.getVouchers()
            }

            // Lọc ngay tại đây
            flow
                .map { pagingData ->
                    pagingData.filter { it.minOrderPrice <= totalAmount }
                }
                .cachedIn(viewModelScope)
                .collect {
                    _vouchers.value = it
                }
        }

    }




    fun onSelectedVoucherToCheckout(voucher: Voucher){
        viewModelScope.launch {
            _event.send(VoucherCheckEvents.OnBackToCheckout(voucher))
        }
    }


    sealed class VoucherCheckState {
        data object Nothing : VoucherCheckState()
        data object Loading : VoucherCheckState()
        data object Success : VoucherCheckState()
        data object Error : VoucherCheckState()
    }

    sealed class VoucherCheckEvents {
        data class  OnBackToCheckout(val voucher: Voucher): VoucherCheckEvents()
    }


}