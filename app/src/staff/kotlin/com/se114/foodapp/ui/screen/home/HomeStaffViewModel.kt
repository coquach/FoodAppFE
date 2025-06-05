package com.se114.foodapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Menu
import com.example.foodapp.domain.use_case.food.GetFoodsByMenuIdUseCase
import com.example.foodapp.domain.use_case.food.GetMenusUseCase
import com.se114.foodapp.domain.use_case.cart.AddToCartUseCase
import com.se114.foodapp.domain.use_case.cart.GetCartSizeUseCase
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
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class HomeStaffViewModel @Inject constructor(
    private val getFoodsByMenuIdUseCase: GetFoodsByMenuIdUseCase,
    private val getMenusUseCase: GetMenusUseCase,
    private val getCartSizeUseCase: GetCartSizeUseCase,
    private val addToCartUseCase: AddToCartUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeStaffState.UiState())
    val uiState: StateFlow<HomeStaffState.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<HomeStaffState.Event>()
    val event get() = _event.receiveAsFlow()

    init {
        getCartSize()
    }

    val foodFlows = mutableMapOf<Long, StateFlow<PagingData<Food>>>()

    fun getFoodsByMenuId(menuId: Long = 1): StateFlow<PagingData<Food>> {
        return foodFlows.getOrPut(menuId) {
            getFoodsByMenuIdUseCase.invoke(menuId)
                .cachedIn(viewModelScope)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
                )

        }
    }

    val menus: StateFlow<PagingData<Menu>> = getMenusUseCase.invoke()
        .cachedIn(viewModelScope).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PagingData.empty()
        )


    private fun getCartSize() {
        viewModelScope.launch {
            getCartSizeUseCase.invoke()
                .collect { cartSize ->
                    _uiState.update {
                        it.copy(cartSize = cartSize)
                    }
                }
        }
    }

    private fun addToCart() {
        viewModelScope.launch {


            addToCartUseCase(_uiState.value.foodSelected!!).collect { result ->
                when (result) {
                    AddToCartUseCase.Result.ItemAdded -> {
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        _event.send(HomeStaffState.Event.OnAddToCart)
                    }

                    AddToCartUseCase.Result.ItemUpdated -> {
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        _event.send(HomeStaffState.Event.OnItemAlreadyInCart)
                    }

                    AddToCartUseCase.Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }

                    is AddToCartUseCase.Result.Failure -> {
                        _uiState.update { it.copy(isLoading = false, error = result.errorMessage) }
                        _event.send(HomeStaffState.Event.ShowError)
                    }
                }
            }


        }
    }

    fun onAction(action: HomeStaffState.Action) {
        when (action) {
            is HomeStaffState.Action.OnAddToCart -> {
                addToCart()
            }

            is HomeStaffState.Action.OnCartClicked -> {
                viewModelScope.launch {
                    _event.send(HomeStaffState.Event.GoToCart)
                }
            }

            is HomeStaffState.Action.OnFoodClicked -> {
                _uiState.update {
                    it.copy(foodSelected = action.food.toFoodUiHomeStaffViewModel())
                }
            }

            is HomeStaffState.Action.OnMenuClicked -> {
                _uiState.update {
                    it.copy(menuIdSelected = action.menuId)
                }
            }

            is HomeStaffState.Action.OnChangeQuantity -> {
                _uiState.update {
                    it.copy(
                        foodSelected = it.foodSelected?.copy(
                            quantity = if (action.quantity < 1) 1
                            else if (action.quantity > it.foodSelected.remainingQuantity) it.foodSelected.remainingQuantity
                            else action.quantity
                        )
                    )
                }
            }

        }
    }

}

data class FoodUiHomeStaffModel(
    val id: Long,
    val name: String,
    val description: String,
    val image: String?,
    val price: BigDecimal,
    val totalLike: Int,
    val totalRating: Double,
    val totalFeedback: Int,
    val remainingQuantity: Int,
    val quantity: Int = 1,
)

fun Food.toFoodUiHomeStaffViewModel() = FoodUiHomeStaffModel(
    id = this.id,
    name = this.name,
    description = this.description,
    image = this.images?.firstOrNull()?.url,
    price = this.price,
    totalLike = this.totalLikes,
    totalRating = this.totalRating,
    totalFeedback = this.totalFeedback,
    remainingQuantity = this.remainingQuantity,
)

object HomeStaffState {
    data class UiState(
        val isLoading: Boolean = false,
        val menuIdSelected: Long = 1,
        val cartSize: Int = 0,
        val error: String? = null,
        val foodSelected: FoodUiHomeStaffModel? = null,
    )

    sealed interface Event {
        data object ShowError : Event
        data object GoToCart : Event
        data object OnAddToCart : Event
        data object OnItemAlreadyInCart : Event

    }

    sealed interface Action {
        data class OnMenuClicked(val menuId: Long) : Action
        data class OnFoodClicked(val food: Food) : Action
        data object OnCartClicked : Action
        data class OnChangeQuantity(val quantity: Int) : Action
        data object OnAddToCart : Action

    }
}