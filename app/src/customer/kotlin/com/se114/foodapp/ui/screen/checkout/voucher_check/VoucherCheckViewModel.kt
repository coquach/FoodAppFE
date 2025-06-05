package com.se114.foodapp.ui.screen.checkout.voucher_check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.foodapp.data.dto.filter.VoucherFilter
import com.example.foodapp.data.model.Voucher
import com.se114.foodapp.domain.use_case.cart.GetCheckOutDetailsUseCase
import com.example.foodapp.domain.use_case.voucher.GetVoucherForCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoucherCheckViewModel @Inject constructor(
    private val getCheckoutDetailsUseCase: GetCheckOutDetailsUseCase,
    private val getVoucherForCustomerUseCase: GetVoucherForCustomerUseCase,
) : ViewModel() {


    private val _event = Channel<VoucherCheck.Event>()
    val event = _event.receiveAsFlow()


    private val _vouchers = MutableStateFlow<PagingData<Voucher>>(PagingData.empty())
    val vouchers: StateFlow<PagingData<Voucher>> = _vouchers


    init{
        getVouchers()
    }
    private fun getVouchers() {
        viewModelScope.launch {

            val checkout = getCheckoutDetailsUseCase().first()
            val totalAmount = checkout.subTotal

            getVoucherForCustomerUseCase(
                filter = VoucherFilter()
            )
                .map { pagingData ->
                    pagingData.filter { it.minOrderPrice <= totalAmount }
                }
                .cachedIn(viewModelScope)
                .collect {
                    _vouchers.value = it
                }
        }

    }
    fun onAction(action: VoucherCheck.Action) {
        when (action) {
            VoucherCheck.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(VoucherCheck.Event.OnBack)
                }
            }
            is VoucherCheck.Action.OnVoucherSelected -> {
                viewModelScope.launch {
                    _event.send(VoucherCheck.Event.OnBackToCheckout(action.voucher))
                }
            }
        }
    }

}

object VoucherCheck {
    sealed interface Event {
        data class OnBackToCheckout(val voucher: Voucher) : Event
        data object OnBack : Event
    }

    sealed interface Action {
        data class OnVoucherSelected(val voucher: Voucher) : Action
        data object OnBack : Action

    }
}