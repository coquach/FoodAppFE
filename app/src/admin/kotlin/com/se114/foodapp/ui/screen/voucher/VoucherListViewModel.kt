package com.se114.foodapp.ui.screen.voucher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.VoucherFilter

import com.example.foodapp.data.model.Voucher
import com.se114.foodapp.data.dto.filter.SupplierFilter

import com.se114.foodapp.domain.use_case.voucher.CreateVoucherUseCase
import com.se114.foodapp.domain.use_case.voucher.DeleteVoucherUseCase
import com.se114.foodapp.domain.use_case.voucher.GetVouchersUseCase
import com.se114.foodapp.domain.use_case.voucher.UpdateVoucherUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VoucherListViewModel @Inject constructor(
    private val getVouchersUseCase: GetVouchersUseCase,
    private val createVoucherUseCase: CreateVoucherUseCase,
    private val updateVoucherUseCase: UpdateVoucherUseCase,
    private val deleteVoucherUseCase: DeleteVoucherUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoucherSate.UiState())
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<VoucherSate.Event>()
    val event get() = _event.receiveAsFlow()


    fun getVouchers(filter: VoucherFilter) = getVouchersUseCase(filter)

    private fun createVoucher() {
        viewModelScope.launch {
            createVoucherUseCase(_uiState.value.voucherSelected).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                            )
                        }
                        _event.send(VoucherSate.Event.ShowToastSuccess("Thêm voucher thành công"))
                        onAction(VoucherSate.Action.OnRefresh)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(VoucherSate.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                            )
                        }
                    }
                }
            }
        }

    }

    private fun updateVoucher() {
        viewModelScope.launch {
            updateVoucherUseCase(_uiState.value.voucherSelected).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                            )
                        }
                        _event.send(VoucherSate.Event.ShowToastSuccess("Cập nhật voucher thành công"))
                        onAction(VoucherSate.Action.OnRefresh)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(VoucherSate.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun deleteVoucher() {
        viewModelScope.launch {
            viewModelScope.launch {
                deleteVoucherUseCase(_uiState.value.voucherSelected.id!!).collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                )
                            }
                            _event.send(VoucherSate.Event.ShowToastSuccess("Xóa voucher thành công"))
                            onAction(VoucherSate.Action.OnRefresh)

                        }

                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = response.errorMessage
                                )
                            }
                            _event.send(VoucherSate.Event.ShowError)
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = true,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: VoucherSate.Action) {
        when (action) {
            is VoucherSate.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(VoucherSate.Event.OnBack)
                }
            }

            is VoucherSate.Action.OnVoucherSelected -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = action.voucher
                    )
                }
            }

            is VoucherSate.Action.OnCodeChange -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = it.voucherSelected.copy(
                            code = action.code
                        )
                    )
                }
            }

            is VoucherSate.Action.OnValueChange -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = it.voucherSelected.copy(
                            value = action.value ?: 0.0
                        )
                    )
                }
            }

            is VoucherSate.Action.OnMinOrderPriceChange -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = it.voucherSelected.copy(
                            minOrderPrice = action.minOrderPrice ?: BigDecimal.ZERO
                        )
                    )
                }
            }

            is VoucherSate.Action.OnMaxValueChange -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = it.voucherSelected.copy(
                            maxValue = action.maxValue ?: BigDecimal.ZERO
                        )
                    )
                }
            }

            is VoucherSate.Action.OnQuantityChange -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = it.voucherSelected.copy(
                            quantity = action.quantity ?: 0
                        )
                    )
                }
            }

            is VoucherSate.Action.OnTypeChange -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = it.voucherSelected.copy(
                            type = action.type
                        )
                    )
                }
            }

            is VoucherSate.Action.OnStartDateChange -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = it.voucherSelected.copy(
                            startDate = action.startDate
                        )
                    )
                }
            }

            is VoucherSate.Action.OnEndDateChange -> {
                _uiState.update {
                    it.copy(
                        voucherSelected = it.voucherSelected.copy(
                            endDate = action.endDate
                        )
                    )
                }
            }

            is VoucherSate.Action.OnAddVoucher -> {
                createVoucher()
            }

            is VoucherSate.Action.OnUpdateVoucher -> {
                updateVoucher()
            }

            is VoucherSate.Action.OnDeleteVoucher -> {
                deleteVoucher()
            }

            is VoucherSate.Action.OnUpdateStatus -> {
                _uiState.update {
                    it.copy(
                        isUpdating = action.isUpdating
                    )
                }
            }

            is VoucherSate.Action.OnRefresh -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(forceRefresh = UUID.randomUUID().toString())
                    )
                }

            }

            is VoucherSate.Action.OnNameSearch -> {
                _uiState.update {
                    it.copy(
                        nameSearch = action.name
                    )
                }
            }

            is VoucherSate.Action.OnOrderChange -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(order = action.order)
                    )
                }
            }

            VoucherSate.Action.OnSearchFilter -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(
                            code = _uiState.value.nameSearch,
                        )
                    )
                }
            }

            is VoucherSate.Action.OnSortByChange -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(sortBy = action.sort)
                    )
                }
            }

            is VoucherSate.Action.OnDateChange -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(
                            startDate = action.startDate,
                            endDate = action.endDate
                        )
                    )
                }
            }
        }

    }
}

object VoucherSate {
    data class UiState(
        val filter: VoucherFilter = VoucherFilter(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val voucherSelected: Voucher = Voucher(),
        val isUpdating: Boolean = false,
        val nameSearch: String = "",
    )

    sealed interface Event {
        data object OnBack : Event
        data object ShowError : Event
        data class ShowToastSuccess(val message: String) : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnVoucherSelected(val voucher: Voucher) : Action
        data class OnCodeChange(val code: String) : Action
        data class OnValueChange(val value: Double?) : Action
        data class OnMinOrderPriceChange(val minOrderPrice: BigDecimal?) : Action
        data class OnMaxValueChange(val maxValue: BigDecimal?) : Action
        data class OnQuantityChange(val quantity: Int?) : Action
        data class OnTypeChange(val type: String) : Action
        data class OnStartDateChange(val startDate: LocalDate?) : Action
        data class OnEndDateChange(val endDate: LocalDate?) : Action
        data object OnAddVoucher : Action
        data object OnUpdateVoucher : Action
        data object OnDeleteVoucher : Action
        data class OnUpdateStatus(val isUpdating: Boolean) : Action
        data object OnRefresh : Action
        data class OnNameSearch(val name: String) : Action
        data object OnSearchFilter : Action
        data class OnOrderChange(val order: String) : Action
        data class OnSortByChange(val sort: String) : Action
        data class OnDateChange(val startDate: LocalDate?, val endDate: LocalDate?) : Action

    }
}