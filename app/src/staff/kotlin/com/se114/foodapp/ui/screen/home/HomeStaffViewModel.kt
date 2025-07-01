package com.se114.foodapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.FoodTableFilter
import com.example.foodapp.data.model.FoodTable
import com.example.foodapp.data.model.enums.FoodTableStatus
import com.example.foodapp.domain.use_case.food_table.GetFoodTablesUseCase
import com.se114.foodapp.domain.use_case.food_table.CreateOrderForTableUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeStaffViewModel @Inject constructor(
    private val getFoodTablesUseCase: GetFoodTablesUseCase,
    private val createOrderForTableUseCase: CreateOrderForTableUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeStaffState.UiState())
    val uiState: StateFlow<HomeStaffState.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<HomeStaffState.Event>()
    val event get() = _event.receiveAsFlow()

    fun getFoodTables(filter: FoodTableFilter) = getFoodTablesUseCase(filter)



    private fun createOrderForTable(){
        viewModelScope.launch {
            createOrderForTableUseCase(
                id = _uiState.value.foodTableSelected?.id!!,

            ).collect{result->
                when(result){
                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                error = result.errorMessage,
                                isLoading = false
                            )}
                        _event.send(HomeStaffState.Event.ShowError)
                    }
                    ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is ApiResponse.Success -> {
                        delay(1000L)
                        _event.send(HomeStaffState.Event.NavigateToCheckout(_uiState.value.foodTableSelected?.id!!))
                    }
                }
            }
        }
    }


    fun onAction(action: HomeStaffState.Action) {
        when (action) {


            HomeStaffState.Action.OnRefresh -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(forceRefresh = UUID.randomUUID().toString())
                    )
                }
            }
            is HomeStaffState.Action.OnSearch -> {
                _uiState.update {
                    it.copy(nameSearch = action.name)
                }
            }
            is HomeStaffState.Action.OnOrderChange -> {
                _uiState.update {
                    it.copy(filter = it.filter.copy(order = action.order))
                }
            }
            is HomeStaffState.Action.OnSortByChange -> {
                _uiState.update {
                    it.copy(filter = it.filter.copy(sortBy = action.sortBy))
                }
            }
            is HomeStaffState.Action.OnNameSearchChange -> {
                _uiState.update {
                    it.copy(nameSearch = action.name)
                }
            }
            HomeStaffState.Action.OnSearchFilter -> {
                _uiState.update {
                    it.copy(filter = it.filter.copy(tableNumber =_uiState.value.nameSearch.toIntOrNull()))
                }
            }

            is HomeStaffState.Action.OnStatusChange -> {
                _uiState.update {
                    it.copy(filter = it.filter.copy(status = action.status))
                }
            }
            is HomeStaffState.Action.OnFoodTableSelected -> {
                _uiState.update {
                    it.copy(foodTableSelected = action.foodTable)
                }
            }

            is HomeStaffState.Action.OnNavigateToOrder -> {
                viewModelScope.launch {
                    _event.send(HomeStaffState.Event.NavigateToCheckout(action.id))
                }
            }
            HomeStaffState.Action.CreateOrderForTable -> {
                createOrderForTable()
            }
        }
    }

}



//fun Food.toFoodUiHomeStaffViewModel() = FoodUiHomeStaffModel(
//    id = this.id,
//    name = this.name,
//    description = this.description,
//    image = this.images?.firstOrNull()?.url,
//    price = this.price,
//    totalLike = this.totalLikes,
//    totalRating = this.totalRating,
//    totalFeedback = this.totalFeedback,
//    remainingQuantity = this.remainingQuantity,
//)

object HomeStaffState {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val nameSearch: String = "",
        val filter: FoodTableFilter = FoodTableFilter(status = FoodTableStatus.EMPTY.name),
        val foodTableSelected: FoodTable?=null,
    )

    sealed interface Event {
        data object ShowError : Event
        data class NavigateToCheckout(val id: Int) : Event

    }

    sealed interface Action {
        data object OnRefresh : Action
        data class OnSearch(val name: String) : Action
        data class OnOrderChange(val order: String) : Action
        data class OnSortByChange(val sortBy: String) : Action
        data class OnNameSearchChange(val name: String) : Action
        data object OnSearchFilter : Action
        data class OnStatusChange(val status: String): Action
        data class OnFoodTableSelected(val foodTable: FoodTable) : Action
        data class OnNavigateToOrder(val id: Int) : Action
        data object CreateOrderForTable : Action

    }
}