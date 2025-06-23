package com.se114.foodapp.ui.screen.vouchers


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.VoucherFilter
import com.example.foodapp.data.model.Voucher

import com.example.foodapp.domain.use_case.voucher.GetVoucherForCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VouchersViewModel @Inject constructor(
    private val getVoucherForCustomerUseCase: GetVoucherForCustomerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(Vouchers.UiState())
    val uiState: StateFlow<Vouchers.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<Vouchers.Event>()
    val event = _event.receiveAsFlow()


    val vouchers =
        getVoucherForCustomerUseCase(VoucherFilter())

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
    data class UiState(
        val filter: VoucherFilter = VoucherFilter(),
    )

    sealed interface Event {
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnBack : Action
    }
}