package com.se114.foodapp.ui.screen.favorite

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
import com.example.foodapp.utils.TabCacheManager
import com.se114.foodapp.domain.use_case.food_favorite.GetFavoriteFoodUseCase
import com.se114.foodapp.ui.screen.favorite.FavoriteState.MenuSate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteFoodUseCase: GetFavoriteFoodUseCase,
    private val getFoodsByMenuIdUseCase: GetFoodsByMenuIdUseCase,
    private val getMenusUseCase: GetMenusUseCase,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(FavoriteState.UiState(foodFilter = FoodFilter(menuId = 1, status = true)))
    val uiState: StateFlow<FavoriteState.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<FavoriteState.Event>()
    val event get() = _event.receiveAsFlow()

    private val _favoriteFoods = MutableStateFlow<PagingData<Food>>(PagingData.empty())
    val favoriteFoods get() = _favoriteFoods.asStateFlow()

    private fun getFavoriteFoods() {
        viewModelScope.launch {
            getFavoriteFoodUseCase.invoke(FoodFilter(menuId = null)).cachedIn(viewModelScope).collect { result ->
                _favoriteFoods.value = result
        }
    }}

    private fun getMenus() {
        viewModelScope.launch {
            getMenusUseCase.invoke().collect { result ->
                when (result) {
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(menuState = MenuSate.Loading) }
                    }

                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                menus = result.data,
                                menuState = MenuSate.Success
                            )
                        }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(menuState = MenuSate.Error(result.errorMessage)) }
                    }
                }
            }
        }
    }
    val foodsTabManager = TabCacheManager<Int, Food>(
        scope = viewModelScope,
        getFilter = { menuId ->

            _uiState.value.foodFilter.copy(menuId = menuId)
        },
        loadData = { filter ->
            getFoodsByMenuIdUseCase(filter as FoodFilter)
        }
    )

    fun getFoodsFlow(menuId: Int) {
        return foodsTabManager.getFlowForTab(menuId)
    }

    init {
        getMenus()
        getFoodsFlow(1)
        getFavoriteFoods()

    }

    fun onAction(action: FavoriteState.Action) {
        when (action) {
            is FavoriteState.Action.OnFoodClick -> {
                viewModelScope.launch {
                    _event.send(FavoriteState.Event.NavigateToDetail(action.food))
                }}
            is FavoriteState.Action.OnChangeNameSearch -> {
                _uiState.update { it.copy(nameSearch = action.name) }
            }
            is FavoriteState.Action.OnMenuClicked -> {
                _uiState.update { it.copy(foodFilter = it.foodFilter.copy(menuId = action.id), menuName = action.name) }
            }
            FavoriteState.Action.OnRefresh -> {
                foodsTabManager.refreshAllTabs()
                getFoodsFlow(_uiState.value.foodFilter.menuId!!)
            }

        }
    }
}

object FavoriteState {
    data class UiState(
        val foodFilter: FoodFilter = FoodFilter(),
        val menuName: String?=null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val menus: List<Menu> = emptyList(),
        val menuState: MenuSate = MenuSate.Loading,
        val nameSearch: String = "",
    )

    sealed interface MenuSate {
        data object Loading : MenuSate
        data object Success : MenuSate
        data class Error(val message: String) : MenuSate
    }

    sealed interface Event {
        object ShowError : Event
        data class NavigateToDetail(val food: Food) : Event
    }

    sealed interface Action {
        data class OnFoodClick(val food: Food) : Action
        data class OnChangeNameSearch(val name: String) : Action
        data class OnMenuClicked(val id: Int, val name: String) : Action
        data object OnRefresh : Action
    }

}