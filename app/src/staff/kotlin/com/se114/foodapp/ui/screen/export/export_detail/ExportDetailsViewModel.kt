package com.se114.foodapp.ui.screen.export.export_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.data.model.Export
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.model.Staff
import com.example.foodapp.domain.use_case.inventory.GetInventoriesUseCase
import com.example.foodapp.domain.use_case.staff.GetStaffUseCase
import com.example.foodapp.navigation.ExportDetails
import com.example.foodapp.navigation.exportNavType
import com.se114.foodapp.data.mapper.toExportDetailUiModel
import com.se114.foodapp.domain.use_case.export.CreateExportUseCase
import com.se114.foodapp.domain.use_case.export.UpdateExportUseCase
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
import kotlin.reflect.typeOf

@HiltViewModel
class ExportDetailsViewModel @Inject constructor(

    private val getStaffUseCase: GetStaffUseCase,
    private val getInventoriesUseCase: GetInventoriesUseCase,
    private val createExportUseCase: CreateExportUseCase,
    private val updateExportUseCase: UpdateExportUseCase,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val arguments = savedStateHandle.toRoute<ExportDetails>(
        typeMap = mapOf(typeOf<Export>() to exportNavType)
    )
    private val export = arguments.export
    private val exportDetails = export.exportDetails.map {
        it.toExportDetailUiModel()
    }
    val isEditable = export.exportDate?.plusDays(1)?.isAfter(LocalDate.now()) == true


    private val isUpdated = arguments.isUpdating
    private val _uiState = MutableStateFlow(
        ExportDetailsState.UiState(
            export = export,
            exportDetails = exportDetails,
            isUpdating = isUpdated,
            isEditable = isEditable
        )
    )
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<ExportDetailsState.Event>()
    val event get() = _event.receiveAsFlow()


    val staffs: StateFlow<PagingData<Staff>> =
        getStaffUseCase.invoke(StaffFilter()).cachedIn(viewModelScope).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), PagingData.empty()
        )

    val inventories: StateFlow<PagingData<Inventory>> =
        getInventoriesUseCase.invoke(InventoryFilter()).cachedIn(viewModelScope).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000), PagingData.empty()
        )


    private fun addExportDetails() {
        val newExportDetails = _uiState.value.exportDetailsSelected
        _uiState.update {
            it.copy(
                exportDetails = it.exportDetails + newExportDetails
            )
        }
    }

    private fun updateExportDetails() {
        val updatedExportDetails = _uiState.value.exportDetailsSelected
        _uiState.update {
            it.copy(
                exportDetails = it.exportDetails.map { exportDetails ->
                    if (exportDetails.localId == _uiState.value.exportDetailsSelected.localId) {
                        updatedExportDetails
                    } else {
                        exportDetails
                    }
                })
        }

    }

    private fun deleteExportDetails(exportDetailsId: String) {
        _uiState.update {
            it.copy(
                exportDetails = it.exportDetails.filter { exportDetails ->
                    exportDetails.localId != exportDetailsId
                }
            )
        }
    }

    private fun addExport() {
        viewModelScope.launch {
            createExportUseCase.invoke(_uiState.value.export, _uiState.value.exportDetails)
                .collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.send(ExportDetailsState.Event.BackToAfterModify)
                        }

                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = response.errorMessage
                                )
                            }
                            _event.send(ExportDetailsState.Event.ShowError)
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
        }
    }

    private fun updateExport() {
        viewModelScope.launch {
            updateExportUseCase.invoke(_uiState.value.export, _uiState.value.exportDetails)
                .collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.send(ExportDetailsState.Event.BackToAfterModify)
                        }

                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = response.errorMessage
                                )
                            }
                            _event.send(ExportDetailsState.Event.ShowError)
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }

                    }
                }
        }
    }

    fun onAction(action: ExportDetailsState.Action) {
        when (action) {
            is ExportDetailsState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(ExportDetailsState.Event.GoBack)
                }
            }

            is ExportDetailsState.Action.OnChangeInventory -> {
                _uiState.update {
                    it.copy(
                        exportDetailsSelected = it.exportDetailsSelected.copy(
                            inventoryId = action.inventory.id,
                            ingredientName = action.inventory.ingredientName
                        )
                    )
                }
            }

            is ExportDetailsState.Action.OnChangeQuantity -> {
                _uiState.update {
                    it.copy(
                        exportDetailsSelected = it.exportDetailsSelected.copy(
                            quantity =
                                if (action.quantity < BigDecimal.ONE) {
                                    BigDecimal.ONE
                                } else if (action.quantity > it.exportDetailsSelected.quantityMaximum) {
                                    it.exportDetailsSelected.quantityMaximum
                                } else {
                                    action.quantity
                                }
                        )
                    )
                }
            }

            is ExportDetailsState.Action.OnChangeStaffId -> {
                _uiState.update {
                    it.copy(
                        export = it.export.copy(staffId = action.staffId)
                    )
                }
            }

            is ExportDetailsState.Action.OnChangeExpiryDate -> {
                _uiState.update {
                    it.copy(
                        exportDetailsSelected = it.exportDetailsSelected.copy(expiryDate = action.expiryDate)
                    )
                }
            }

            is ExportDetailsState.Action.OnChangeCost -> {
                _uiState.update {
                    it.copy(
                        exportDetailsSelected = it.exportDetailsSelected.copy(
                            cost = action.cost ?: BigDecimal.ZERO
                        )
                    )
                }
            }

            is ExportDetailsState.Action.AddExportDetails -> {
                addExportDetails()
            }

            is ExportDetailsState.Action.UpdateExportDetails -> {
                updateExportDetails()
            }

            is ExportDetailsState.Action.DeleteExportDetails -> {
                deleteExportDetails(exportDetailsId = action.exportDetailsId)
            }

            is ExportDetailsState.Action.CreateExport -> {
                addExport()
            }

            is ExportDetailsState.Action.UpdateExport -> {
                updateExport()
            }
            is ExportDetailsState.Action.NotifyCantEdit -> {
                viewModelScope.launch {
                    _event.send(ExportDetailsState.Event.NotifyCantEdit)
                }
            }
            is ExportDetailsState.Action.OnExportDetailsSelected -> {
                _uiState.update {
                    it.copy(
                        exportDetailsSelected = action.exportDetails
                    )
                }
            }

        }

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

object ExportDetailsState {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val exportDetails: List<ExportDetailUIModel> = emptyList(),
        val export: Export,
        val exportDetailsSelected: ExportDetailUIModel = ExportDetailUIModel(),
        val isUpdating: Boolean = false,
        val isEditable: Boolean = true,
    )

    sealed interface Event {
        data object GoBack : Event
        data object ShowError : Event
        data object BackToAfterModify : Event
        data object NotifyCantEdit : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnExportDetailsSelected(val exportDetails: ExportDetailUIModel) : Action
        data class OnChangeStaffId(val staffId: Long) : Action
        data class OnChangeInventory(val inventory: Inventory) : Action
        data class OnChangeQuantity(val quantity: BigDecimal) : Action
        data class OnChangeExpiryDate(val expiryDate: LocalDate) : Action
        data class OnChangeCost(val cost: BigDecimal?) : Action
        data object AddExportDetails : Action
        data object UpdateExportDetails : Action
        data class DeleteExportDetails(val exportDetailsId: String) : Action
        data object CreateExport : Action
        data object UpdateExport : Action
        data object NotifyCantEdit : Action
    }
}