package com.se114.foodapp.ui.screen.food_table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.FoodTableFilter
import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.data.model.Staff
import com.example.foodapp.domain.use_case.food_table.GetFoodTablesUseCase
import com.example.foodapp.domain.use_case.food_table.UpdateFoodTableStatusUseCase
import com.example.foodapp.utils.TabCacheManager
import com.google.android.play.integrity.internal.ac
import com.se114.foodapp.domain.use_case.food_table.CreateFoodTableUseCase
import com.se114.foodapp.domain.use_case.food_table.DeleteFoodTableUseCase
import com.se114.foodapp.domain.use_case.food_table.UpdateFoodTableUseCase
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
import javax.inject.Inject

@HiltViewModel
class FoodTableViewModel @Inject constructor(
    private val getFoodTablesUseCase: GetFoodTablesUseCase,
    private val createFoodTableUseCase: CreateFoodTableUseCase,
    private val updateFoodTableUseCase: UpdateFoodTableUseCase,
    private val updateFoodTableStatusUseCase: UpdateFoodTableStatusUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        FoodTableState.UiState(
            foodTableFilter = FoodTableFilter(active = true)
        )
    )
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<FoodTableState.Event>()
    val event get() = _event.receiveAsFlow()

    val foodTablesTabManager = TabCacheManager<Int, FoodTable>(
        scope = viewModelScope,
        getFilter = { tabIndex ->
            val status = getFoodTableStatusForTab(tabIndex)
            _uiState.value.foodTableFilter.copy(active = status)
        },
        loadData = { filter ->
            getFoodTablesUseCase(filter as FoodTableFilter)
        }
    )

    fun getFoodTablesFlow(tabIndex: Int) {
        return foodTablesTabManager.getFlowForTab(tabIndex)
    }


    private fun getFoodTableStatusForTab(tabIndex: Int): Boolean? {
        return when (tabIndex) {
            0 -> true
            1 -> false
            else -> null
        }
    }

    private fun createFoodTable() {
        viewModelScope.launch {
            createFoodTableUseCase.invoke(_uiState.value.foodTableSelected).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }

                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                        _event.send(FoodTableState.Event.ShowToast("Tạo bàn ăn thành công"))
                        onAction(FoodTableState.Action.OnRefresh)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(FoodTableState.Event.ShowError)
                    }
                }

            }
        }
    }

    private fun updateFoodTable() {
        viewModelScope.launch {
            updateFoodTableUseCase.invoke(_uiState.value.foodTableSelected).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }

                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                        _event.send(FoodTableState.Event.ShowToast("Cập nhật bàn ăn thành công"))
                        onAction(FoodTableState.Action.OnRefresh)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(FoodTableState.Event.ShowError)
                    }
                }

            }
        }
    }


    private fun updateStatusTable() {
        viewModelScope.launch {
            updateFoodTableStatusUseCase.invoke(_uiState.value.foodTableSelected.id!!)
                .collect { response ->
                    when (response) {
                        is ApiResponse.Loading -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }

                        is ApiResponse.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                            _event.send(FoodTableState.Event.ShowToast("Cập nhật trạng thái bàn thành công"))
                            onAction(FoodTableState.Action.OnRefresh)
                        }

                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = response.errorMessage
                                )
                            }
                            _event.send(FoodTableState.Event.ShowError)
                        }
                    }

                }
        }

    }

    fun onAction(action: FoodTableState.Action) {
        when (action) {
            is FoodTableState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(FoodTableState.Event.OnBack)
                }
            }

            is FoodTableState.Action.OnCreate -> {
                createFoodTable()
            }

            is FoodTableState.Action.OnUpdate -> {
                updateFoodTable()
            }

            is FoodTableState.Action.OnSetActive -> {
                updateStatusTable()
            }


            is FoodTableState.Action.OnFoodTableSelected -> {
                _uiState.update {
                    it.copy(
                        foodTableSelected = action.foodTable
                    )
                }
            }

            is FoodTableState.Action.OnChangeTableNumber -> {
                _uiState.update {
                    it.copy(
                        foodTableSelected = it.foodTableSelected.copy(
                            tableNumber = action.tableNumber ?: 0
                        )
                    )
                }
            }

            is FoodTableState.Action.OnChangeSeatCapacity -> {
                _uiState.update {
                    it.copy(
                        foodTableSelected = it.foodTableSelected.copy(
                            seatCapacity = action.seatCapacity
                        )
                    )
                }
            }

            is FoodTableState.Action.OnUpdateStatus -> {
                _uiState.update {
                    it.copy(
                        isUpdating = action.isUpdating
                    )
                }
            }

            FoodTableState.Action.OnRefresh -> {
                foodTablesTabManager.refreshAllTabs()
                getFoodTablesFlow(_uiState.value.tabIndex)
            }

            is FoodTableState.Action.OnTabSelected -> {
                _uiState.update {
                    it.copy(
                        tabIndex = action.tabIndex
                    )
                }
            }
        }
    }

}


object FoodTableState {
    data class UiState(
        val tabIndex: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isUpdating: Boolean = false,
        val foodTableSelected: FoodTable = FoodTable(),
        val foodTableFilter: FoodTableFilter = FoodTableFilter(),
    )

    sealed interface Event {
        data object OnBack : Event
        data object ShowError : Event
        data class ShowToast(val message: String) : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnUpdateStatus(val isUpdating: Boolean) : Action
        data class OnFoodTableSelected(val foodTable: FoodTable) : Action
        data class OnChangeTableNumber(val tableNumber: Int?) : Action
        data class OnChangeSeatCapacity(val seatCapacity: Int) : Action
        data object OnCreate : Action
        data object OnUpdate : Action
        data object OnSetActive : Action
        data object OnRefresh : Action
        data class OnTabSelected(val tabIndex: Int) : Action

    }
}