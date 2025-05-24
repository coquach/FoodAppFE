package com.se114.foodapp.ui.screen.vouchers


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.model.Voucher

import com.se114.foodapp.domain.use_case.voucher.GetVoucherForCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VouchersViewModel @Inject constructor(
    private val getVoucherForCustomerUseCase: GetVoucherForCustomerUseCase,
) : ViewModel() {


    private val _event = Channel<Vouchers.Event>()
    val event = _event.receiveAsFlow()


    val vouchers: StateFlow<PagingData<Voucher>> =
        getVoucherForCustomerUseCase().cachedIn(viewModelScope).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PagingData.empty()
        )

    fun onAction(action: Vouchers.Action) {
        when (action) {
            is Vouchers.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(Vouchers.Event.OnBack)
                }
            }
        }
    }
}

object Vouchers {
    sealed interface Event {
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
    }
}