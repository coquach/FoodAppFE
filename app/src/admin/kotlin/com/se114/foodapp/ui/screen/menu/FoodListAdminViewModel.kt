package com.se114.foodapp.ui.screen.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
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


    fun  getFoods(filter: FoodFilter) = getFoodsByMenuIdUseCase.invoke(filter)

    private fun toggleStatusFood() {
        viewModelScope.launch {
            toggleStatusFoodUseCase.invoke(_uiState.value.selectedFood!!.id).collect { result ->
                when (result) {
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(FoodListAdmin.Event.ShowToast("Cập nhật trạng thái thành công"))
                        onAction(FoodListAdmin.Action.OnRefresh)


                    }

                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(isLoading = false, error = result.errorMessage) }
                        _event.send(FoodListAdmin.Event.ShowError)
                    }

                }
            }
        }

    }

    fun getMenus() {
        viewModelScope.launch {
            getMenusUseCase.invoke().collect { result ->
                when (result) {
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(menuState = FoodListAdmin.MenuSate.Loading) }
                    }

                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                menus = result.data,
                                menuState = FoodListAdmin.MenuSate.Success
                            )
                        }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(menuState = FoodListAdmin.MenuSate.Error(result.errorMessage)) }
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
                _uiState.update { it.copy(foodFilter = it.foodFilter.copy(menuId = action.id), menuName = action.name) }

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
                _uiState.update { it.copy(foodFilter = it.foodFilter.copy(forceRefresh = UUID.randomUUID().toString())) }
            }
            is FoodListAdmin.Action.OnChangeNameSearch -> {
                _uiState.update { it.copy(nameSearch = action.name) }
            }
            is FoodListAdmin.Action.OnOrderChange -> {
                _uiState.update { it.copy(foodFilter = it.foodFilter.copy(order = action.order)) }
            }
            is FoodListAdmin.Action.OnSortByChange -> {
                _uiState.update { it.copy(foodFilter = it.foodFilter.copy(sortBy = action.sortBy)) }
            }
            is FoodListAdmin.Action.OnSearchFilter -> {
                _uiState.update { it.copy(foodFilter = it.foodFilter.copy(name = _uiState.value.nameSearch)) }
            }
            is FoodListAdmin.Action.OnChangeStatusFood -> {
                _uiState.update { it.copy(foodFilter = it.foodFilter.copy(status = action.status)) }
            }



        }
    }

}



object FoodListAdmin {
    data class UiState(
        val nameSearch: String = "",
        val menuName: String ?= null,
        val foodFilter: FoodFilter = FoodFilter(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedFood: Food? = null,

        val menus: List<Menu> = emptyList(),
        val menuState: MenuSate = MenuSate.Loading,
    )

    sealed interface MenuSate {
        data object Loading : MenuSate
        data object Success : MenuSate
        data class Error(val message: String) : MenuSate
    }

    sealed interface Event {
        data object ShowError : Event
        data class GoToUpdateFood(val food: Food) : Event
        data object GoToAddFood : Event
        data class ShowToast(val message: String) : Event


    }

    sealed interface Action {
        data class OnChangeStatusFood(val status: Boolean) : Action
        data class OnOrderChange(val order: String) : Action
        data class OnSortByChange(val sortBy: String) : Action
        data class OnChangeNameSearch(val name: String) : Action
        data object OnSearchFilter : Action
        data object OnToggleStatusFood : Action
        data class OnFoodClicked(val food: Food) : Action
        data class OnMenuClicked(val id: Int, val name: String) : Action
        data object OnAddClicked : Action
        data class OnFoodSelected(val food: Food) : Action
        data object OnRefresh : Action


    }
}