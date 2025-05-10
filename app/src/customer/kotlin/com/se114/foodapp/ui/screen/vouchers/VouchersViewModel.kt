package com.se114.foodapp.ui.screen.vouchers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.VoucherRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.CheckoutDetails
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.VoucherType
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class VouchersViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val accountService: AccountService,
    private val voucherRepository: VoucherRepository,

) : BaseViewModel() {

    private val _uiState = MutableStateFlow<VouchersState>(VouchersState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _isLoadingAction = MutableStateFlow(false)
    val isLoadingAction = _isLoadingAction.asStateFlow()

    private val _event = Channel<VouchersEvents>()
    val event = _event.receiveAsFlow()


    private val _vouchers = MutableStateFlow<PagingData<Voucher>>(PagingData.empty())
    val vouchers: StateFlow<PagingData<Voucher>> = _vouchers


    private var isMyVoucher by mutableStateOf(false)

    fun setMode(mode: Boolean) {
        isMyVoucher = mode
        getVouchers()

    }

    private fun getVouchers() {
        viewModelScope.launch {
            delay(100)
            if (isMyVoucher) {
                val customerId = accountService.currentUserId
                voucherRepository.getVouchersByCustomerId(customerId!!).cachedIn(viewModelScope).collect {
                    _vouchers.value = it
                }
            } else {

                voucherRepository.getVouchers().cachedIn(viewModelScope).collect {
                    _vouchers.value = it
                }
            }
        }

    }

    fun receivedVoucher(voucherId: Long) {
        viewModelScope.launch {
            _isLoadingAction.value = true
            try {
                val customerId =
                    accountService.currentUserId ?: throw Exception("Không tìm thấy user")
                val response = safeApiCall { foodApi.receiveVoucher(customerId, voucherId) }
                when (response) {
                    is ApiResponse.Error -> {
                        error = "Lỗi khi lấy voucher"
                        errorDescription = response.message
                        _uiState.value = VouchersState.Error
                    }

                    is ApiResponse.Exception -> {
                        error = "Lỗi khi lấy voucher"
                        errorDescription = response.exception.toString()
                        _uiState.value = VouchersState.Error
                    }

                    is ApiResponse.Success -> {
                        _isLoadingAction.value = false
                        _event.send(VouchersEvents.ShowSuccessToast)
                        getVouchers()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }




    sealed class VouchersState {
        data object Nothing : VouchersState()
        data object Loading : VouchersState()
        data object Success : VouchersState()
        data object Error : VouchersState()
    }

    sealed class VouchersEvents {
        data object ShowSuccessToast : VouchersEvents()
    }


}