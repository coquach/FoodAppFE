package com.se114.foodapp.ui.screen.menu

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.data.dto.filter.FoodFilter
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuState>(MenuState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<MenuEvents>()
    val event get() = _event.receiveAsFlow()

    private val _tabIndex = MutableStateFlow(0)
    val tabIndex: StateFlow<Int> = _tabIndex

    fun setTab(index: Int) {
        _tabIndex.value = index
    }
    fun refreshAllTabs() {
        FoodsCache.clear()

    }
    private val FoodsCache = mutableMapOf<Int, StateFlow<PagingData<Food>>>()


    fun getFoodsByTab(index: Int): StateFlow<PagingData<Food>> {
        return FoodsCache.getOrPut(index) {
            val isAvailable = when (index) {
                0 -> true
                1 -> false
                else -> null
            }

            val filter = FoodFilter(isAvailable = isAvailable)

            FoodRepository.getFoodsByFilter(filter)
                .cachedIn(viewModelScope)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
                )
        }
    }

    private val _selectedItems = mutableStateListOf<Food>()
    val selectedItems: List<Food> get() = _selectedItems

    fun toggleSelection(Food: Food) {
        if (_selectedItems.contains(Food)) {
            _selectedItems.remove(Food)
        } else {
            _selectedItems.add(Food)
        }
    }

    fun selectAllItems(Foods: List<Food>, isSelectAll: Boolean) {
        _selectedItems.clear()
        if (isSelectAll) _selectedItems.addAll(Foods)
    }

    fun removeItem() {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onRemoveClicked() {
        viewModelScope.launch {
            _event.send(MenuEvents.ShowDeleteDialog)
        }
    }

    sealed class MenuState {
        data object Nothing : MenuState()
        data object Loading : MenuState()
        data object Success :
            MenuState()

        data object Error : MenuState()
    }

    sealed class MenuEvents {
        data class ShowErrorMessage(val message: String) : MenuEvents()
        data object ShowDeleteDialog : MenuEvents()
    }
}