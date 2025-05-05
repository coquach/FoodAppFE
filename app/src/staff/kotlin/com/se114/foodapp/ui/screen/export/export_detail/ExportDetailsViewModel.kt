package com.se114.foodapp.ui.screen.export.export_detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.dto.request.ExportDetailRequest
import com.example.foodapp.data.dto.request.ExportRequest
import com.example.foodapp.data.dto.request.ImportDetailRequest
import com.example.foodapp.data.dto.request.ImportRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Export
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.repository.InventoryRepository
import com.example.foodapp.data.repository.StaffRepository
import com.example.foodapp.utils.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExportDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val staffRepository: StaffRepository,
    private val inventoryRepository: InventoryRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<ExportDetailsState>(ExportDetailsState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ExportDetailsEvents>()
    val event = _event.receiveAsFlow()


    private val _staffs = MutableStateFlow<PagingData<Staff>>(PagingData.empty())
    val staffs = _staffs

    private val _inventories = MutableStateFlow<PagingData<Inventory>>(PagingData.empty())
    val inventories = _inventories

    private val _exportRequest = MutableStateFlow(
        ExportRequest(
            staffId = null,
            exportDate = "",
            exportDetails = emptyList()
        )
    )
    val exportRequest = _exportRequest.asStateFlow()


    fun onChangStaffId(staffId: Long) {
        _exportRequest.update { it.copy(staffId = staffId) }
    }


    private val _exportDetailsListRequest = MutableStateFlow<List<ExportDetailUIModel>>(emptyList())
    val exportDetailsListRequest = _exportDetailsListRequest.asStateFlow()


    private val _exportDetailsRequest = MutableStateFlow(
        ExportDetailUIModel()
    )
    val exportDetailsRequest = _exportDetailsRequest.asStateFlow()

    fun onChangeQuantity(quantity: BigDecimal) {
        _exportDetailsRequest.update { it.copy(quantity = quantity) }

    }

    fun onChangeInventory(inventory: Inventory) {
        _exportDetailsRequest.update {
            it.copy(
                inventoryId = inventory.id,
                ingredientName = inventory.ingredientName,
                expiryDate = inventory.expiryDate?: LocalDate.now(),
                quantityMaximum = inventory.quantityRemaining
            )
        }

    }

    fun loadExportDetails(exportDetail: ExportDetailUIModel? = null) {
        if (exportDetail != null) {
            _exportDetailsRequest.update {
                it.copy(
                    localId = exportDetail.localId,
                    id = exportDetail.id,
                    ingredientName = exportDetail.ingredientName,
                    expiryDate = exportDetail.expiryDate,
                    quantity = exportDetail.quantity,
                    quantityMaximum = exportDetail.quantityMaximum,
                    cost = exportDetail.cost
                )
            }
        } else {
            _exportDetailsRequest.update {
                it.copy(
                    localId = UUID.randomUUID().toString(),
                    id = null,
                    ingredientName = null,
                    expiryDate = LocalDate.now(),
                    quantity = BigDecimal(1),
                    quantityMaximum = BigDecimal(1),
                    cost = BigDecimal(0)
                )
            }
        }
    }

    private var isUpdating by mutableStateOf(false)

    init {
        viewModelScope.launch {
            coroutineScope {
                val inventoriesDeferred = async { getInventories() }
                val staffsDeferred = async { getStaffs() }

                // Chờ các API hoàn thành
                inventoriesDeferred.await()
                staffsDeferred.await()
            }
        }

    }

    fun setMode(mode: Boolean, export: Export?) {
        isUpdating = mode

        if (isUpdating && export != null) {
            loadInitialData(export)
        }
    }

    private fun loadInitialData(export: Export) {
        _exportRequest.update {
            it.copy(

                staffId = export.staffId,
                exportDate = StringUtils.formatLocalDate(export.exportDate)?: "",
            )
        }
        _exportDetailsListRequest.update {
            export.exportDetails.map { detail ->
                ExportDetailUIModel(
                    localId = UUID.randomUUID().toString(),
                    id = detail.id,
                    ingredientName = detail.ingredientName,
                    expiryDate = detail.expiryDate,
                    quantity = detail.quantity,
                    cost = detail.cost
                )
            }
        }
    }

    private fun getStaffs() {
        viewModelScope.launch {
            staffRepository.getAllStaffs().cachedIn(viewModelScope).collect {
                _staffs.value = it
            }
        }
    }

    private fun getInventories() {
        viewModelScope.launch {
            inventoryRepository.getInventoriesByFilter(InventoryFilter(isOutOfStock = false))
                .cachedIn(viewModelScope).collect {
                    _inventories.value = it
                }
        }
    }


    fun addExportDetails() {
        val newExportDetails = _exportDetailsRequest.value
        _exportDetailsListRequest.update { oldList ->
            oldList + newExportDetails
        }
        loadExportDetails()
    }

    fun updateExportDetails(exportDetailsId: String) {
        val updatedExportDetails = _exportDetailsRequest.value
        _exportDetailsListRequest.update { oldList ->
            oldList.map { exportDetails ->
                if (exportDetails.localId == exportDetailsId) updatedExportDetails else exportDetails
            }
        }
        loadExportDetails()
    }

    fun deleteExportDetails(exportDetailsId: String) {
        _exportDetailsListRequest.update { oldList ->
            oldList.filterNot { exportDetails ->
                exportDetails.localId == exportDetailsId
            }
        }
    }

    fun addExport() {
        viewModelScope.launch {
            _uiState.value = ExportDetailsState.Loading
            _exportRequest.update {
                it.copy(
                    exportDate = StringUtils.getFormattedCurrentVietnamDate(),
                    exportDetails = _exportDetailsListRequest.value.map { exportDetails ->
                        ExportDetailRequest(
                            id = exportDetails.id,
                            inventoryId = exportDetails.inventoryId,
                            quantity = exportDetails.quantity,
                        )
                    }
                )
            }
            val request = _exportRequest.value
            delay(3000)

            try {
                val response = safeApiCall { foodApi.createExport(request) }
                when (response) {
                    is ApiResponse.Success -> {

                        _uiState.value = ExportDetailsState.Success
                        _event.send(ExportDetailsEvents.GoBack)
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = ExportDetailsState.Error("Tạo phiếu xuất thất bại")
                        Log.d("Add import", response.message)
                    }

                    else -> {
                        _uiState.value = ExportDetailsState.Error("Tạo phiếu xuất thất bại")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ExportDetailsState.Error("Tạo phiếu xuất thất bại")
                Log.d("Add import:", e.message.toString())
            }
        }
    }

    fun updateExport(exportId: Long) {
        viewModelScope.launch {
            _uiState.value = ExportDetailsState.Loading
            _exportRequest.update {
                it.copy(
                    exportDate = StringUtils.getFormattedCurrentVietnamDate(),
                    exportDetails = _exportDetailsListRequest.value.map { exportDetails ->
                        ExportDetailRequest(
                            id = exportDetails.id,
                            inventoryId = exportDetails.inventoryId,
                            quantity = exportDetails.quantity,
                        )
                    }
                )
            }
            val request = _exportRequest.value
            delay(3000)

            try {
                val response = safeApiCall { foodApi.updateExport(exportId, request) }
                when (response) {
                    is ApiResponse.Success -> {

                        _uiState.value = ExportDetailsState.Success
                        _event.send(ExportDetailsEvents.GoBack)
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = ExportDetailsState.Error("Cập nhật phiếu xuất thất bại")
                        Log.d("Update import", response.message)
                    }

                    else -> {
                        _uiState.value = ExportDetailsState.Error("Cập nhật phiếu xuất thất bại")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ExportDetailsState.Error("Cập nhật phiếu xuất thất bại")
                Log.d("Update import:", e.message.toString())
            }
        }
    }

    sealed class ExportDetailsState {
        data object Nothing : ExportDetailsState()
        data object Loading : ExportDetailsState()
        data object Success : ExportDetailsState()
        data class Error(val message: String) : ExportDetailsState()
    }

    sealed class ExportDetailsEvents {
        data object GoBack : ExportDetailsEvents()
    }
}

data class ExportDetailUIModel(
    val localId: String = UUID.randomUUID().toString(),
    val id: Long? = null,
    val inventoryId: Long? = null,
    val ingredientName: String? = null,
    val expiryDate: LocalDate = LocalDate.now(),
    val quantity: BigDecimal = BigDecimal(1),
    val quantityMaximum: BigDecimal = BigDecimal(1),
    val cost: BigDecimal = BigDecimal.ZERO,
)