package com.se114.foodapp.ui.screen.setting.supplier

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.dto.request.SupplierRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.remote.FoodApi
import com.google.android.gms.common.api.Api
import com.se114.foodapp.data.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val supplierRepository: SupplierRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<SupplierState>(SupplierState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<SupplierEvents>()
    val event = _event.receiveAsFlow()

    private val _suppliersActive = MutableStateFlow<PagingData<Supplier>>(PagingData.empty())
    val suppliersActive = _suppliersActive

    private val _suppliersHidden = MutableStateFlow<PagingData<Supplier>>(PagingData.empty())
    val suppliersHidden = _suppliersHidden

    init {
        getSuppliers()
    }

    private fun getSuppliers() {
        viewModelScope.launch {

            supplierRepository.getAllSuppliers(SupplierFilter(isActive = true))
                .cachedIn(viewModelScope)
                .collect { _suppliersActive.value = it }
        }

        viewModelScope.launch {

            supplierRepository.getAllSuppliers(SupplierFilter(isActive = false))
                .cachedIn(viewModelScope)
                .collect { _suppliersHidden.value = it }
        }

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
                        getSuppliers()
                        _uiState.value = SupplierState.Success
                        loadSupplier()
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = SupplierState.Error
                        errorDescription = response.message
                        Log.d("Supplier error", response.message)
                    }

                    else -> {
                        _uiState.value = SupplierState.Error
                        errorDescription = "Lỗi không xác định"

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
                        getSuppliers()
                        _uiState.value = SupplierState.Success
                        loadSupplier()
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = SupplierState.Error
                        errorDescription = response.message
                    }

                    else -> {
                        _uiState.value = SupplierState.Error
                        errorDescription = "Lỗi không xác định"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SupplierState.Error
                errorDescription = e.message.toString()
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
                        getSuppliers()
                        _uiState.value = SupplierState.Success
                        _supplierRequest.value
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = SupplierState.Error
                        errorDescription = response.message
                    }

                    else -> {
                        _uiState.value = SupplierState.Error
                        errorDescription = "Lỗi không xác định"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SupplierState.Error
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

    sealed class SupplierEvents {
        data object OnBack : SupplierEvents()
        data object ShowSupplierDialog : SupplierEvents()
        data object ShowErrorSheet : SupplierEvents()
        data object ShowActiveDialog : SupplierEvents()
        data object ShowDeleteDialog : SupplierEvents()

    }
}