package com.se114.foodapp.ui.screen.warehouse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.domain.use_case.inventory.GetInventoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class WarehouseViewModel @Inject constructor(
    private val getInventoriesUseCase: GetInventoriesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WarehouseState.UiState())
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<WarehouseState.Event>()
    val event get() = _event.receiveAsFlow()

    val inventories = getInventoriesUseCase(_uiState.value.inventoryFilter)

    fun onAction(action: WarehouseState.Action) {
        when (action) {
            is WarehouseState.Action.OnTabSelected -> {
                _uiState.update {
                   it.copy(
                       inventoryFilter = action.filter,

                   )
                }}
            WarehouseState.Action.OnRefresh -> {
                _uiState.update {
                    it.copy(
                        inventoryFilter = it.inventoryFilter.copy(
                            ingredientName = _uiState.value.nameSearch,
                        )
                    )
                }

            }
            WarehouseState.Action.OnNavigateToImport -> {
                viewModelScope.launch {
                    _event.send(WarehouseState.Event.NavigateToImport)
                }
            }
            WarehouseState.Action.OnNavigateToMaterial -> {
                viewModelScope.launch {
                    _event.send(WarehouseState.Event.NavigateToMaterial)
                }
            }
            is WarehouseState.Action.OnNameSearch -> {
                _uiState.update {
                    it.copy(
                        nameSearch = action.name
                    )}}
            is WarehouseState.Action.OnOrderChange -> {
                _uiState.update {
                    it.copy(
                        inventoryFilter = it.inventoryFilter.copy(
                            order = action.order))}}
            is WarehouseState.Action.OnSortByChange -> {
                _uiState.update {
                    it.copy(
                        inventoryFilter = it.inventoryFilter.copy(
                            sortBy = action.sortBy))}}
            WarehouseState.Action.OnSearchFilter -> {}
        }
    }

}
object WarehouseState{
    data class UiState(
        val inventoryFilter: InventoryFilter= InventoryFilter(),
        val nameSearch: String = "",
    )
    sealed interface Event{
        data object Refresh: Event
        data object NavigateToImport: Event
        data object NavigateToMaterial: Event
    }
    sealed interface Action{
        data class OnNameSearch(val name: String): Action
        data object OnSearchFilter: Action
        data class OnOrderChange(val order: String): Action
        data class OnSortByChange(val sortBy: String): Action
        data class OnTabSelected(val filter: InventoryFilter): Action
        data object OnRefresh: Action
        data object OnNavigateToImport: Action
        data object OnNavigateToMaterial: Action

    }
}