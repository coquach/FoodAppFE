package com.se114.foodapp.ui.screen.warehouse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn

import com.example.foodapp.data.model.Inventory

import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.data.model.Staff
import com.example.foodapp.domain.use_case.inventory.GetInventoriesUseCase
import com.example.foodapp.utils.TabCacheManager

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

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

//    private val inventoriesCache = mutableMapOf<Int, StateFlow<PagingData<Inventory>>>()
//
//    private fun refreshAllTabs() {
//        inventoriesCache.clear()
//    }
//
//    fun getInventoriesByTab(index: Int): StateFlow<PagingData<Inventory>> {
//        return inventoriesCache.getOrPut(index) {
//            val status = when (index) {
//                0 -> true
//                1 -> false
//                else -> true
//            }
//
//            val filter = InventoryFilter()
//
//            getInventoriesUseCase.invoke(filter)
//                .cachedIn(viewModelScope)
//                .stateIn(
//                    viewModelScope,
//                    SharingStarted.WhileSubscribed(5000),
//                    PagingData.empty()
//                )
//        }
//    }


    val inventoriesTabManager = TabCacheManager<Int, Inventory>(
        scope = viewModelScope,
        getFilter = { tabIndex ->
            val filter = getInventoriesStatusForTab(tabIndex)
            _uiState.value.inventoryFilter.copy(isExpired = filter?.isExpired, isOutOfStock = filter?.isOutOfStock)
        },
        loadData = { filter ->
            getInventoriesUseCase(filter as InventoryFilter)
        }
    )

    fun getInventoriesFlow(tabIndex: Int){
        return inventoriesTabManager.getFlowForTab(tabIndex)
    }
    init {
        getInventoriesFlow(0)
    }

    private fun getInventoriesStatusForTab(tabIndex: Int): InventoryFilter? {
        return when (tabIndex) {
            0 -> InventoryFilter()
            1-> InventoryFilter(isExpired = true, isOutOfStock = false)
            2-> InventoryFilter(isOutOfStock = true, isExpired = false)
            else -> null
        }
    }

    fun onAction(action: WarehouseState.Action) {
        when (action) {
            is WarehouseState.Action.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(
                    tabIndex = action.index
                )}
            WarehouseState.Action.OnRefresh -> {
                inventoriesTabManager.refreshAllTabs()
                getInventoriesFlow(_uiState.value.tabIndex)
            }
        }
    }

}
object WarehouseState{
    data class UiState(
        val tabIndex: Int = 0,
        val inventoryFilter: InventoryFilter= InventoryFilter(),
    )
    sealed interface Event{
        data object Refresh: Event
    }
    sealed interface Action{
        data class OnTabSelected(val index: Int): Action
        data object OnRefresh: Action
    }
}