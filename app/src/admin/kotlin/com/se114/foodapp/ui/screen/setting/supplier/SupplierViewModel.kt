package com.se114.foodapp.ui.screen.setting.supplier

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.FoodFilter
import com.example.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Supplier
import com.google.android.gms.common.api.Api
import com.se114.foodapp.data.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SupplierViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val supplierRepository: SupplierRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<SupplierState>(SupplierState.Nothing)
    val uiState = _uiState.asStateFlow()



    private val _filter = MutableStateFlow(SupplierFilter())

    private fun updateFilter(newFilter: SupplierFilter) {
        _filter.update { it.copy(reloadTrigger = newFilter.reloadTrigger) }
    }



    val suppliers: StateFlow<PagingData<Supplier>> = _filter
        .flatMapLatest { filter ->
            supplierRepository.getAllSuppliers(filter)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    fun setTab(index: Int) {
        val newFilter = when (index) {
            0 -> SupplierFilter(isActive = true)
            1 -> SupplierFilter(isActive = false)
            else -> SupplierFilter()
        }
        _filter.value = newFilter
    }

    private val _supplierRequest = MutableStateFlow(
        SupplierRequest(
            name = "",
            phone = "",
            email = "",
            address = ""
        )
    )
    val supplierRequest = _supplierRequest.asStateFlow()
    fun loadSupplier(supplier: Supplier?=null) {
        if(supplier!=null){
            _supplierRequest.update {
                it.copy(
                    name = supplier.name,
                    phone = supplier.phone,
                    email = supplier.email,
                    address = supplier.address
                )
            }
        }
        else {
            _supplierRequest.update {
                it.copy(
                    name = "",
                    phone = "",
                    email = "",
                    address = ""
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _supplierRequest.update { it.copy(name = name) }
    }
    fun onPhoneChange(phone: String) {
        _supplierRequest.update { it.copy(phone = phone) }
    }
    fun onEmailChange(email: String) {
        _supplierRequest.update { it.copy(email = email) }
    }
    fun onAddressChange(address: String) {
        _supplierRequest.update { it.copy(address = address) }
    }


    fun addSupplier() {
        viewModelScope.launch {
            _uiState.value = SupplierState.Loading
            try {
                val supplierRequest = _supplierRequest.value
                val response = safeApiCall { foodApi.createSupplier(supplierRequest) }
                when (response) {
                    is ApiResponse.Success -> {
                        updateFilter(SupplierFilter())
                        _uiState.value = SupplierState.Success
                        loadSupplier()
                    }

                    is ApiResponse.Error -> {

                        errorDescription = response.message
                        _uiState.value = SupplierState.Error
                    }

                    else -> {
                        error = "Đã xảy ra lỗi ..."
                        errorDescription = "Lỗi không xác định"
                        _uiState.value = SupplierState.Error

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SupplierState.Error
                errorDescription = "${e.message}"
                Log.d("Supplier error", "${e.message}" )
            }
        }
    }
    fun updateSupplier(supplierId: Long){
        viewModelScope.launch {
            _uiState.value = SupplierState.Loading
            try {
                val supplierRequest = _supplierRequest.value
                val response = safeApiCall { foodApi.updateSupplier(supplierId, supplierRequest) }
                when (response) {
                    is ApiResponse.Success -> {
                        updateFilter(SupplierFilter())
                        _uiState.value = SupplierState.Success
                        loadSupplier()
                    }

                    is ApiResponse.Error -> {

                        errorDescription = response.message
                        _uiState.value = SupplierState.Error
                    }

                    else -> {

                        error = "Đã xảy ra lỗi ..."
                        errorDescription = "Lỗi không xác định"
                        _uiState.value = SupplierState.Error
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                error = "Đã xảy ra lỗi ..."
                errorDescription = e.message.toString()
                _uiState.value = SupplierState.Error
            }
        }
    }

    fun setActiveSupplier(supplierId: Long, isActive:Boolean) {
        viewModelScope.launch {
            _uiState.value = SupplierState.Loading
            try {
                val supplierRequest = _supplierRequest.value
                val response = safeApiCall { foodApi.setActiveSupplier(supplierId, isActive) }
                when (response) {
                    is ApiResponse.Success -> {
                        updateFilter(SupplierFilter())
                        _uiState.value = SupplierState.Success
                        _supplierRequest.value
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = SupplierState.Error
                        errorDescription = response.message
                    }

                    else -> {
                        error = "Đã xảy ra lỗi ..."

                        errorDescription = "Lỗi không xác định"
                        _uiState.value = SupplierState.Error
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SupplierState.Error
                error = "Đã xảy ra lỗi ..."
                errorDescription = e.message.toString()
            }
        }
    }



    sealed class SupplierState {
        data object Nothing : SupplierState()
        data object Loading : SupplierState()
        data object Success : SupplierState()
        data object Error : SupplierState()
    }


}