package com.se114.foodapp.ui.screen.warehouse

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.repository.InventoryRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class WarehouseViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<WarehouseState>(WarehouseState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<WarehouseEvents>()
    val event = _event.receiveAsFlow()

    private val _filter = MutableStateFlow(InventoryFilter())


    val inventories:  StateFlow<PagingData<Inventory>> = _filter
        .flatMapLatest { filter ->
            inventoryRepository.getInventoriesByFilter(filter)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PagingData.empty()
        )

    fun setTab(index: Int) {
        val newFilter = when (index) {
            0 -> InventoryFilter()
            2 -> InventoryFilter(isOutOfStock = true)
            else -> InventoryFilter()
        }
        _filter.value = newFilter
    }
    fun onRemoveClicked() {
        viewModelScope.launch {
            _event.send(WarehouseEvents.ShowDeleteDialog)
        }
    }

    sealed class WarehouseState {
        data object Nothing : WarehouseState()
        data object Loading : WarehouseState()
        data object SuccessImport : WarehouseState()
        data class SuccessInventory(val inventoryList: List<Inventory>)

        data class Error(val message: String) : WarehouseState()
    }

    sealed class WarehouseEvents {
        data object NavigateToDetail : WarehouseEvents()
        data object ShowDeleteDialog : WarehouseEvents()
    }
}