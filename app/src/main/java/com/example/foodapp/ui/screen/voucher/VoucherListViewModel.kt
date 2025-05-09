package com.example.foodapp.ui.screen.voucher

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.dto.request.VoucherRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.model.Voucher
import com.example.foodapp.data.model.enums.VoucherType
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.repository.VoucherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class VoucherListViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val voucherRepository: VoucherRepository,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<VoucherListState>(VoucherListState.Nothing)
    val uiState = _uiState.asStateFlow()


    private val _vouchers = MutableStateFlow<PagingData<Voucher>>(PagingData.empty())
    val vouchers: StateFlow<PagingData<Voucher>> = _vouchers

    init {
        getVouchers()
    }

    private fun getVouchers() {
        viewModelScope.launch {
            voucherRepository.getVouchers().collect {
                _vouchers.value = it
            }
        }
    }

    private val _voucherRequest = MutableStateFlow(
        VoucherRequest(
            code = "",
            value = 0.0,
            minOrderPrice = BigDecimal(0),
            maxValue = BigDecimal(0),
            total = 0,
            type = VoucherType.PERCENTAGE.name,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            published = true
        )
    )
    val voucherRequest = _voucherRequest.asStateFlow()
    fun loadVoucher(voucher: Voucher? = null) {
        if (voucher != null) {
            _voucherRequest.update {
                it.copy(
                    code = voucher.code,
                    value = voucher.value,
                    minOrderPrice = voucher.minOrderPrice,
                    maxValue = voucher.maxValue,
                    total = voucher.total,
                    type = voucher.type,
                    startDate = voucher.startDate,
                    endDate = voucher.endDate,
                )
            }
        } else {
            _voucherRequest.update {
                it.copy(
                    code = "",
                    value = 0.0,
                    minOrderPrice = BigDecimal(0),
                    maxValue = BigDecimal(0),
                    total = 0,
                    type = VoucherType.PERCENTAGE.name,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now(),
                    published = true
                )
            }
        }
    }

    fun onCodeChange(code: String) {
        _voucherRequest.update { it.copy(code = code) }
    }

    fun onValueChange(value: String) {
        _voucherRequest.update { it.copy(value = value.toDoubleOrNull() ?: 0.0) }
    }

    fun onMinOrderPriceChange(minOrderPrice: String) {

            _voucherRequest.update { it.copy(minOrderPrice = minOrderPrice.toBigDecimalOrNull() ?: BigDecimal(0)) }

    }
    fun onMaxValueChange(maxValue: String){
        _voucherRequest.update { it.copy(maxValue = maxValue.toBigDecimalOrNull() ?: BigDecimal(0)) }
    }

    fun onTotalChange(total: String) {
        _voucherRequest.update { it.copy(total = total.toIntOrNull() ?: 0) }
    }

    fun onStartDateChange(startDate: LocalDate?) {
        _voucherRequest.update { it.copy(startDate = startDate) }
    }

    fun onEndDateChange(endDate: LocalDate?) {
        _voucherRequest.update { it.copy(endDate = endDate) }
    }

    fun onTypeChange(type : String) {
        _voucherRequest.update { it.copy(type = type) }
    }

    fun onPublishedChange(published: Boolean) {
        _voucherRequest.update { it.copy(published = published) }
    }

    fun addVoucher() {
        viewModelScope.launch {
            _uiState.value = VoucherListState.Loading
            try {
                val voucherRequest = _voucherRequest.value
                val response = safeApiCall { foodApi.createVouchers(voucherRequest) }
                when (response) {
                    is ApiResponse.Success -> {
                        getVouchers()
                        _uiState.value = VoucherListState.Success
                        loadVoucher()
                    }

                    is ApiResponse.Error -> {

                        error = "Lỗi tạo voucher"
                        errorDescription = response.message
                        _uiState.value = VoucherListState.Error
                    }

                    else -> {
                        error = "Đã xảy ra lỗi ..."
                        errorDescription = "Lỗi không xác định"
                        _uiState.value = VoucherListState.Error

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error = "Đã xảy ra lỗi ..."

                errorDescription = "${e.message}"
                _uiState.value = VoucherListState.Error

            }
        }
    }

    fun updateVoucher(voucherId: Long) {
        viewModelScope.launch {
            _uiState.value = VoucherListState.Loading
            try {
                val voucherRequest = _voucherRequest.value
                val response = safeApiCall { foodApi.updateVoucher(voucherId, voucherRequest) }
                when (response) {
                    is ApiResponse.Success -> {
                        getVouchers()
                        _uiState.value = VoucherListState.Success
                        loadVoucher()
                    }

                    is ApiResponse.Error -> {
                        error = "Lỗi cập nhật voucher"
                        errorDescription = response.message
                        _uiState.value = VoucherListState.Error
                    }

                    else -> {
                        error = "Đã xảy ra lỗi ..."
                        errorDescription = "Lỗi không xác định"
                        _uiState.value = VoucherListState.Error
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error = "Đã xảy ra lỗi ..."
                errorDescription = e.message.toString()
                _uiState.value = VoucherListState.Error
            }
        }
    }

    fun deleteVoucher(voucherId: Long){
        viewModelScope.launch {
            _uiState.value = VoucherListState.Loading
            try {
                val response = safeApiCall { foodApi.deleteVoucher(voucherId) }
                when (response) {
                    is ApiResponse.Success -> {
                        getVouchers()
                        _uiState.value = VoucherListState.Success
                    }

                    is ApiResponse.Error -> {
                        error = "Lỗi xóa voucher"
                        errorDescription = response.message
                        _uiState.value = VoucherListState.Error
                    }

                    else -> {
                        error = "Đã xảy ra lỗi ..."
                        errorDescription = "Lỗi không xác định"
                        _uiState.value = VoucherListState.Error
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error = "Đã xảy ra lỗi ..."
                errorDescription = e.message.toString()
                _uiState.value = VoucherListState.Error
            }
        }
    }


    sealed class VoucherListState {
        data object Nothing : VoucherListState()
        data object Loading : VoucherListState()
        data object Success : VoucherListState()
        data object Error : VoucherListState()
    }


}