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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VouchersViewModel @Inject constructor(
    private val getVoucherForCustomerUseCase: GetVoucherForCustomerUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(Vouchers.UiState())
    val uiState: StateFlow<Vouchers.UiState> get() = _uiState.asStateFlow()


    private val _event = Channel<Vouchers.Event>()
    val event get() = _event.receiveAsFlow()


    fun getVouchers(filter: VoucherFilter) = getVoucherForCustomerUseCase(filter)

    fun onAction(action: Vouchers.Action) {
        when (action) {
            is Vouchers.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(Vouchers.Event.OnBack)
                }
            }
            is Vouchers.Action.OnChangeNameSearch -> {
                _uiState.update { it.copy(nameSearch = action.name) }
            }
            is Vouchers.Action.OnChangeOrder -> {
                _uiState.update { it.copy(filter = it.filter.copy(order = action.order)) }
            }
            is Vouchers.Action.OnChangeSortByName -> {
                _uiState.update { it.copy(filter = it.filter.copy(sortBy = action.sort)) }
            }
            Vouchers.Action.OnSearchFilter -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(
                            code = _uiState.value.nameSearch,
                        ))
                }
            }
            Vouchers.Action.OnRefresh -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(forceRefresh = UUID.randomUUID().toString())

                    )
                }
            }
        }
    }

}

object Vouchers {
    data class UiState(
        val filter: VoucherFilter = VoucherFilter(),
        val nameSearch: String = "",
    )

    sealed interface Event {
        data object OnBack : Event
    }

    sealed interface Action {
        data object OnRefresh : Action
        data object OnBack : Action
        data class OnChangeNameSearch(val name: String) : Action
        data class OnChangeOrder(val order: String) : Action
        data class OnChangeSortByName(val sort: String) : Action
        data object OnSearchFilter : Action

    }
}