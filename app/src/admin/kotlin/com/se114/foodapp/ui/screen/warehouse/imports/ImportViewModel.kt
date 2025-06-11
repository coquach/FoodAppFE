package com.se114.foodapp.ui.screen.warehouse.imports


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Import
import com.se114.foodapp.data.dto.filter.ImportFilter

import com.se114.foodapp.domain.use_case.imports.DeleteImportUseCase
import com.se114.foodapp.domain.use_case.imports.GetImportsUseCase
import com.se114.foodapp.ui.screen.warehouse.imports.ImportState.Event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val getImportsUseCase: GetImportsUseCase,
    private val deleteImportUseCase: DeleteImportUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportState.UiState())
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<ImportState.Event>()
    val event get() = _event.receiveAsFlow()

    private val  _imports = MutableStateFlow<PagingData<Import>>(PagingData.empty())
    val imports get() = _imports.asStateFlow()

    init {
        getImports()
    }
    private fun getImports() {
        viewModelScope.launch {
            getImportsUseCase(_uiState.value.filter).cachedIn(viewModelScope).collect {
                _imports.value = it
            }
        }
    }

    private fun deleteImport() {
        viewModelScope.launch {
            deleteImportUseCase(_uiState.value.importSelected!!).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _event.send(ImportState.Event.ShowSuccess("Xoá đơn nhập thành công"))
                        _event.send(ImportState.Event.Refresh)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _event.send(ImportState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }
    fun onAction(action: ImportState.Action) {
        when (action) {
            ImportState.Action.OnDelete -> {
                deleteImport()
            }
            is ImportState.Action.OnImportSelected -> {
                _uiState.value = _uiState.value.copy(importSelected = action.importId)
            }

            ImportState.Action.OnRefresh -> {
                viewModelScope.launch {
                    _event.send(Refresh)
                }
            }

            is ImportState.Action.OnImportClick -> {
                viewModelScope.launch {
                    _event.send(GoToImportDetails(action.import))
                }
            }

            ImportState.Action.AddImport -> {
                viewModelScope.launch {
                    _event.send(AddImport)
                }
            }

            ImportState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(OnBack)
                }
            }

            ImportState.Action.NotifyCantDelete -> {
                viewModelScope.launch {
                    _event.send(NotifyCantDelete)
                }
            }
        }
    }
}

object ImportState {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val importSelected: Long? = null,
        val filter: ImportFilter = ImportFilter(),
    )

    sealed interface Event {
        data object ShowError : Event
        data class ShowSuccess(val message: String) : Event
        data object Refresh : Event
        data class GoToImportDetails(val import: Import) : Event
        data object AddImport : Event
        data object OnBack : Event
        data object NotifyCantDelete : Event
    }

    sealed interface Action {
        data object OnDelete : Action
        data class OnImportSelected(val importId: Long) : Action
        data object OnRefresh : Action
        data class OnImportClick(val import: Import) : Action
        data object AddImport : Action
        data object OnBack : Action
        data object NotifyCantDelete : Action


    }
}