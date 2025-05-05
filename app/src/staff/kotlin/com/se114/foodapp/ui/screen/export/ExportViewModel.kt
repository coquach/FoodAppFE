package com.se114.foodapp.ui.screen.export

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Export
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.remote.FoodApi
import com.se114.foodapp.data.dto.filter.ExportFilter
import com.se114.foodapp.data.repository.ExportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val exportRepository: ExportRepository
) : ViewModel() {
    private val _export = MutableStateFlow<PagingData<Export>>(PagingData.empty())
    val exportList = _export

    init {
        refreshExports()
    }

    fun refreshExports() {
        viewModelScope.launch {
            exportRepository.getAllExports(ExportFilter()).cachedIn(viewModelScope).collect{
                _export.value = it
            }
        }
    }

    private val _uiState = MutableStateFlow<ExportState>(ExportState.Nothing)
    val uiState = _uiState.asStateFlow()


    private val _event = Channel<ExportEvents>()
    val event = _event.receiveAsFlow()


    fun removeExport(exportId: Long) {
        viewModelScope.launch {
            _uiState.value = ExportState.Loading
            try {
                val response = safeApiCall { foodApi.deleteExport(exportId) }
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.value = ExportState.Success
                        _event.send(ExportEvents.ShowSuccessToast("Xóa phiếu xuất thành công"))
                    }
                    is ApiResponse.Error -> {
                        _uiState.value = ExportState.Error("Xóa phiếu xuất thất bại")
                        Log.d("Delete import", response.message)
                    }
                    else -> {
                        _uiState.value = ExportState.Error("Xóa phiếu xuất thất bại")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Api error: ", "${e.message}")
                _uiState.value = ExportState.Error("Có lỗi xảy ra: ${e.message}")
            }
        }
    }

    fun onRemoveSwipe() {
        viewModelScope.launch {
            _event.send(ExportEvents.ShowDeleteDialog)
        }
    }

    sealed class ExportState{
        data object Nothing: ExportState()
        data object Loading: ExportState()
        data object Success: ExportState()
        data class Error(val message: String): ExportState()
    }


    sealed class ExportEvents {
        data class ShowSuccessToast(val message: String): ExportEvents()
        data object ShowDeleteDialog : ExportEvents()

    }
}