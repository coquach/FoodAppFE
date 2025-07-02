package com.se114.foodapp.ui.screen.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Export
import com.se114.foodapp.data.dto.filter.ExportFilter
import com.se114.foodapp.domain.use_case.export.DeleteExportUseCase
import com.se114.foodapp.domain.use_case.export.GetExportsUseCase
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
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val getExportsUseCase: GetExportsUseCase,
    private val deleteExportsUseCase: DeleteExportUseCase,
) : ViewModel() {


    private val _uiState = MutableStateFlow(ExportState.UiState())
    val uiState get() = _uiState.asStateFlow()


    private val _event = Channel<ExportState.Event>()
    val event get() = _event.receiveAsFlow()


    fun getExports(filter: ExportFilter) = getExportsUseCase(filter)


    private fun deleteExport() {
        viewModelScope.launch {
            deleteExportsUseCase.invoke(_uiState.value.exportSelected.id!!).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null
                            )
                        }
                        _event.send(ExportState.Event.ShowSuccessToast("Xóa đơn xuất thành công"))
                        onAction(ExportState.Action.OnRefresh)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(ExportState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: ExportState.Action) {
        when (action) {
            is ExportState.Action.OnExportClicked -> {
                viewModelScope.launch {
                    _event.send(ExportState.Event.GoToDetail(action.export))
                }
            }

            ExportState.Action.AddExport -> {
                viewModelScope.launch {
                    _event.send(ExportState.Event.AddExport)
                }
            }

            ExportState.Action.DeleteExport -> {
                deleteExport()
            }

            ExportState.Action.OnRefresh -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(
                            forceRefresh = UUID.randomUUID().toString()
                        )
                    )
                }
            }

            is ExportState.Action.OnExportSelected -> {
                _uiState.update {
                    it.copy(
                        exportSelected = action.export
                    )
                }
            }

            ExportState.Action.NotifyCantDelete -> {
                viewModelScope.launch {
                    _event.send(ExportState.Event.NotifyCantDelete)
                }
            }

            is ExportState.Action.OnDateChange -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(
                            startDate = action.startDate,
                            endDate = action.endDate,
                        )
                    )
                }
            }

        }
    }

}

object ExportState {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val exportSelected: Export = Export(),
        val filter: ExportFilter = ExportFilter(),

        )

    sealed interface Event {
        data class ShowSuccessToast(val message: String) : Event
        data object ShowError : Event
        data object AddExport : Event
        data class GoToDetail(val export: Export) : Event
        data object NotifyCantDelete : Event


    }

    sealed interface Action {
        data class OnExportClicked(val export: Export) : Action
        data object AddExport : Action
        data object DeleteExport : Action
        data object OnRefresh : Action
        data class OnExportSelected(val export: Export) : Action
        data object NotifyCantDelete : Action
        data class OnDateChange(val startDate: LocalDate?, val endDate: LocalDate?) : Action

    }
}