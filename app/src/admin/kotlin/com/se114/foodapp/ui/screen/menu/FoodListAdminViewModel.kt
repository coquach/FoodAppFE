package com.se114.foodapp.ui.screen.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.FoodFilter
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Menu
import com.example.foodapp.domain.use_case.food.GetFoodsByMenuIdUseCase
import com.example.foodapp.domain.use_case.food.GetMenusUseCase
import com.se114.foodapp.domain.use_case.food.ToggleStatusFoodUseCase
import com.se114.foodapp.ui.screen.menu.FoodListAdmin.Event.GoToAddFood
import com.se114.foodapp.ui.screen.menu.FoodListAdmin.Event.GoToUpdateFood
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
class FoodListAdminViewModel @Inject constructor(
    private val getFoodsByMenuIdUseCase: GetFoodsByMenuIdUseCase,
    private val toggleStatusFoodUseCase: ToggleStatusFoodUseCase,
    private val getMenusUseCase: GetMenusUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodListAdmin.UiState())
    val uiState: StateFlow<FoodListAdmin.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<FoodListAdmin.Event>()
    val event get() = _event.receiveAsFlow()

    private fun refreshAllTabs() {
        foodsCache.clear()
    }

    val menus: StateFlow<PagingData<Menu>> =
        getMenusUseCase.invoke()
            .cachedIn(viewModelScope)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                PagingData.empty()
            )

    private val foodsCache = mutableMapOf<Long, MutableMap<Boolean, StateFlow<PagingData<Food>>>>()


    fun getFoodsByIdAndStatus(index: Int = 0, menuId: Long): StateFlow<PagingData<Food>> {


        val isAvailable = when (index) {
            0 -> true
            1 -> false
            else -> null
        }
        val menuCache = foodsCache.getOrPut(menuId) { mutableMapOf() }
        return menuCache.getOrPut(isAvailable == true) {
            val filter = FoodFilter(isAvailable = isAvailable)
            getFoodsByMenuIdUseCase.invoke(menuId)
                .cachedIn(viewModelScope)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
                )
        }


    }
    private fun toggleStatusFood(){
        viewModelScope.launch {
            toggleStatusFoodUseCase.invoke(_uiState.value.selectedFood!!.id).collect { result ->
                when (result) {
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }


                    }
                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(isLoading = false, error = result.errorMessage) }
                        _event.send(FoodListAdmin.Event.ShowError)
                    }

                }
                }
            }
        }
    fun onAction(action: FoodListAdmin.Action) {
        when (action) {
            is FoodListAdmin.Action.OnToggleStatusFood -> {
                toggleStatusFood()
            }


            is FoodListAdmin.Action.OnFoodClicked -> {
                viewModelScope.launch {
                    _event.send(GoToUpdateFood(action.food))
                }
            }

            is FoodListAdmin.Action.OnMenuClicked -> {
                _uiState.update { it.copy(menuIdSelected = action.id) }

            }
            is FoodListAdmin.Action.OnTabSelected -> {
                _uiState.update { it.copy(tabIndex = action.index) }
            }

            FoodListAdmin.Action.OnAddClicked -> {
                viewModelScope.launch {
                    _event.send(GoToAddFood)
                }
            }

            is FoodListAdmin.Action.OnFoodSelected -> {
                _uiState.update { it.copy(selectedFood = action.food) }
            }

            FoodListAdmin.Action.OnRefresh -> {
                refreshAllTabs()
            }
        }
    }

}

object FoodListAdmin {
    data class UiState(
        val tabIndex: Int = 0,
        val menuIdSelected: Long=1,
        val isLoading: Boolean = false,
        val foods: List<Food> = emptyList(),
        val error: String? = null,
        val selectedFood: Food? = null,
    )

    sealed interface Event {
        data object ShowError : Event
        data class GoToUpdateFood(val food: Food) : Event
        data object GoToAddFood: Event
        data object Refresh : Event

    }

    sealed interface Action {
        data object OnToggleStatusFood : Action
        data class OnFoodClicked(val food: Food) : Action
        data class OnMenuClicked(val id: Long) : Action
        data class OnTabSelected(val index: Int) : Action
        data object OnAddClicked : Action
        data class OnFoodSelected(val food: Food) : Action
        data object OnRefresh : Action
    }
}