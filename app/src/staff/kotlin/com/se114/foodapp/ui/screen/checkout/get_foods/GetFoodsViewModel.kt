package com.se114.foodapp.ui.screen.checkout.get_foods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.FoodFilter
import com.example.foodapp.data.dto.filter.FoodTableFilter
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.model.OrderItem
import com.example.foodapp.domain.use_case.food.GetFoodsByMenuIdUseCase
import com.example.foodapp.domain.use_case.food.GetMenusUseCase
import com.se114.foodapp.ui.screen.checkout.get_foods.GetFoodsState.GetMenusState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class GetFoodsViewModel @Inject constructor(
    private val getFoodsByMenuIdUseCase: GetFoodsByMenuIdUseCase,
    private val getMenusUseCase: GetMenusUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GetFoodsState.UiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<GetFoodsState.Event>()
    val event = _event.receiveAsFlow()

    fun getMenus() {
        viewModelScope.launch {
            getMenusUseCase(status = true).collect { response ->
                when (response) {
                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(getMenusState = GetMenusState.Error(response.errorMessage)) }
                    }

                    ApiResponse.Loading -> {
                        _uiState.update { it.copy(getMenusState = GetMenusState.Loading) }
                    }

                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                menus = response.data,
                                getMenusState = GetMenusState.Success
                            )
                        }
                    }

                }
            }
        }
    }

    fun getFoodsByMenuId(filter: FoodFilter) = getFoodsByMenuIdUseCase(filter)

    private fun addFoodList(food: Food) {
        val foodUi = FoodUiStaffModel(
            id = food.id,
            name = food.name,
            description = food.description,
            image = food.images?.firstOrNull()?.url,
            price = food.price,
            remainingQuantity = food.remainingQuantity,
            quantity = 1,
        )
        _uiState.update { it.copy(foodStaffsUi = it.foodStaffsUi + foodUi) }
    }

    private fun removeFoodUi(id: Long) {
        _uiState.update { it.copy(foodStaffsUi = it.foodStaffsUi.filter { it.id != id }) }
    }

    private fun updateQuantity(id: Long, quantity: Int) {
        _uiState.update {
            it.copy(foodStaffsUi = it.foodStaffsUi.map {
                if (it.id == id) it.copy(
                    quantity = quantity
                ) else it
            })
        }
    }


    fun onAction(action: GetFoodsState.Action) {
        when (action) {
            is GetFoodsState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(GetFoodsState.Event.OnBack)
                }
            }

            is GetFoodsState.Action.OnMenuSelected -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(menuId = action.menuId),
                        menuSelected = action.menuName
                    )
                }

            }

            is GetFoodsState.Action.OnNameSearchChange -> {
                _uiState.update { it.copy(nameSearch = action.name) }
            }

            is GetFoodsState.Action.OnOrderChange -> {
                _uiState.update { it.copy(filter = it.filter.copy(order = action.order)) }
            }

            is GetFoodsState.Action.OnSortByChange -> {
                _uiState.update { it.copy(filter = it.filter.copy(sortBy = action.sortBy)) }
            }

            GetFoodsState.Action.OnSearchFilter -> {
                _uiState.update { it.copy(filter = it.filter.copy(name = it.nameSearch)) }
            }

            is GetFoodsState.Action.AddFood -> {
                addFoodList(action.food)
            }

            is GetFoodsState.Action.RemoveFood -> {
                removeFoodUi(action.id)
            }

            is GetFoodsState.Action.OnQuantityChange -> {
                if (action.quantity == 0) removeFoodUi(action.foodUi.id)
                else if (action.quantity > action.foodUi.remainingQuantity) updateQuantity(
                    action.foodUi.id,
                    action.foodUi.remainingQuantity
                )
                else updateQuantity(action.foodUi.id, action.quantity)
            }

            GetFoodsState.Action.GetFoodToCheckout -> {
                val orderItems = _uiState.value.foodStaffsUi.map {
                    OrderItem(
                        id = it.id,
                        foodName = it.name,
                        price = it.price,
                        foodImage = it.image,
                        foodId = it.id,
                        quantity = it.quantity
                    )
                }
                viewModelScope.launch {
                    _event.send(GetFoodsState.Event.GetFoodToCheckout(orderItems))
                }
            }

        }
    }
}

data class FoodUiStaffModel(
    val id: Long,
    val name: String,
    val description: String,
    val image: String?,
    val price: BigDecimal,
    val remainingQuantity: Int,
    val quantity: Int = 1,

    )

object GetFoodsState {
    data class UiState(
        val menus: List<Menu> = emptyList(),
        val getMenusState: GetMenusState = GetMenusState.Loading,
        val filter: FoodFilter = FoodFilter(menuId= 1),
        val nameSearch: String = "",
        val menuSelected: String? = null,
        val foodStaffsUi: List<FoodUiStaffModel> = emptyList(),
    )

    sealed interface GetMenusState {
        object Loading : GetMenusState
        object Success : GetMenusState
        data class Error(val message: String) : GetMenusState
    }

    sealed interface Event {
        object OnBack : Event
        data class GetFoodToCheckout(val orderItems: List<OrderItem>) : Event
    }

    sealed interface Action {
        data class OnMenuSelected(val menuId: Int, val menuName: String) : Action
        data class OnOrderChange(val order: String) : Action
        data class OnNameSearchChange(val name: String) : Action
        object OnBack : Action
        data class OnSortByChange(val sortBy: String) : Action
        data object OnSearchFilter : Action
        data class AddFood(val food: Food) : Action
        data class RemoveFood(val id: Long) : Action
        data class OnQuantityChange(val foodUi: FoodUiStaffModel, val quantity: Int) : Action
        data object GetFoodToCheckout : Action

    }
}