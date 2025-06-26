package com.se114.foodapp.ui.screen.food_table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.FoodTableFilter
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

) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodTableState.UiState())
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<FoodTableState.Event>()
    val event get() = _event.receiveAsFlow()

    val foodTables: StateFlow<PagingData<FoodTable>> = getFoodTablesUseCase(FoodTableFilter(active = true)).cachedIn(viewModelScope).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = PagingData.empty()
    )


    fun onAction(action: FoodTableState.Action) {
        when (action) {
            is FoodTableState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(FoodTableState.Event.OnBack)
                }
            }







        }
    }

}


object FoodTableState {
    data class UiState(
        val error: String? = null,
    )

    sealed interface Event {
        data object OnBack : Event
        data object ShowError : Event
    }

    sealed interface Action {
        data object OnBack : Action


    }
}