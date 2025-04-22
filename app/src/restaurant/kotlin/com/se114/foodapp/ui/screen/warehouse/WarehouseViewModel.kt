package com.se114.foodapp.ui.screen.warehouse

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Inventory

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarehouseViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<WarehouseState>(WarehouseState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<WarehouseEvents>()
    val event = _event.asSharedFlow()

   

    fun onRemoveClicked() {
        viewModelScope.launch {
            _event.emit(WarehouseEvents.ShowDeleteDialog)
        }
    }

    sealed class WarehouseState {
        data object Nothing : WarehouseState()
        data object Loading : WarehouseState()
        data class SuccessImport(val importList: List<Import>) :
            WarehouseState()
        data class SuccessInventory(val inventoryList: List<Inventory>)

        data class Error(val message: String) : WarehouseState()
    }

    sealed class WarehouseEvents {
        data object NavigateToDetail : WarehouseEvents()
        data object ShowDeleteDialog : WarehouseEvents()
    }
}