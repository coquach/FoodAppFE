package com.se114.foodapp.ui.screen.warehouse.imports

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Staff
import com.se114.foodapp.data.dto.filter.ImportFilter
import com.se114.foodapp.data.repository.ImportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val importRepository: ImportRepository
) : ViewModel() {
    private val _import = MutableStateFlow<PagingData<Import>>(PagingData.empty())
    val importList = _import

    init {
        refreshImports()
    }

    fun refreshImports() {
        viewModelScope.launch {
            importRepository.getAllImports(ImportFilter()).cachedIn(viewModelScope).collect{
                _import.value = it
            }
        }
    }

    private val _uiState = MutableStateFlow<ImportState>(ImportState.Nothing)
    val uiState = _uiState.asStateFlow()


    private val _event = Channel<ImportEvents>()
    val event = _event.receiveAsFlow()


    fun removeImport(importId: Long) {
        viewModelScope.launch {
            _uiState.value = ImportState.Loading
            try {
                val response = safeApiCall { foodApi.deleteImport(importId) }
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.value = ImportState.Success
                        _event.send(ImportEvents.ShowSuccessToast("Xóa phiếu nhập thành công"))
                    }
                    is ApiResponse.Error -> {
                        _uiState.value = ImportState.Error("Xóa phiếu nhập thất bại")
                     Log.d("Delete import", response.message)
                    }
                    else -> {
                        _uiState.value = ImportState.Error("Xóa phiếu nhập thất bại")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Api error: ", "${e.message}")
                _uiState.value = ImportState.Error("Có lỗi xảy ra: ${e.message}")
            }
        }
    }

    fun onRemoveSwipe() {
        viewModelScope.launch {
            _event.send(ImportEvents.ShowDeleteDialog)
        }
    }

    sealed class ImportState{
        data object Nothing: ImportState()
        data object Loading: ImportState()
        data object Success: ImportState()
        data class Error(val message: String): ImportState()
    }


    sealed class ImportEvents {
        data class ShowSuccessToast(val message: String): ImportEvents()
        data object ShowDeleteDialog : ImportEvents()

    }
}