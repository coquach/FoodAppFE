package com.se114.foodapp.ui.screen.food_table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.domain.use_case.food_table.GetFoodTablesUseCase
import com.example.foodapp.domain.use_case.food_table.UpdateFoodTableStatusUseCase
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
   private val updateFoodTableStatusUseCase: UpdateFoodTableStatusUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodTableState.UiState())
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<FoodTableState.Event>()
    val event get() = _event.receiveAsFlow()

    private val foodTablesCache = mutableMapOf<Int, StateFlow<PagingData<FoodTable>>>()

    private fun refreshAllTabs() {
        foodTablesCache.clear()
    }

    fun getFoodTablesByTab(index: Int): StateFlow<PagingData<FoodTable>> {
        return foodTablesCache.getOrPut(index) {
            val status = when (index) {
                0 -> true
                1 -> false
                else -> true
            }

//            val filter = SupplierFilter(isActive = status)

            getFoodTablesUseCase.invoke()
                .cachedIn(viewModelScope)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
                )
        }
    }
    private fun setStatusFoodTable(id: Int, status: Boolean ) {
        viewModelScope.launch {
            updateFoodTableStatusUseCase.invoke(id, status).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _event.send(FoodTableState.Event.ShowToast("Cập nhật trạng thái bàn ăn thành công"))
                        _event.send(FoodTableState.Event.OnRefresh)
                    }
                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                error = response.errorMessage
                            )
                        }
                        _event.send(FoodTableState.Event.ShowError)
                    }
                    is ApiResponse.Loading -> {}

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




            is FoodTableState.Action.OnRefresh -> {
                refreshAllTabs()
            }

            is FoodTableState.Action.OnTabSelected -> {
                _uiState.update {
                    it.copy(
                        tabIndex = action.index
                    )
                }
            }
            is FoodTableState.Action.OnUpdateStatus -> {
                setStatusFoodTable(action.id, action.status)
            }

        }
    }

}


object FoodTableState {
    data class UiState(
        val tabIndex: Int = 0,
        val error: String? = null,
    )

    sealed interface Event {
        data object OnBack : Event
        data object ShowError : Event
        data class ShowToast(val message: String) : Event
        data object OnRefresh : Event
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnUpdateStatus(val id: Int,val status: Boolean) : Action
        data class OnTabSelected(val index: Int) : Action
        data object OnRefresh : Action

    }
}