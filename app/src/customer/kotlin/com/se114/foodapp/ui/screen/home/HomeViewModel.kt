package com.se114.foodapp.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Menu
import com.se114.foodapp.domain.use_case.cart.GetCartSizeUseCase
import com.example.foodapp.domain.use_case.food.GetFoodsByMenuIdUseCase
import com.example.foodapp.domain.use_case.food.GetMenusUseCase

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
class HomeViewModel @Inject constructor(
    private val getFoodsByMenuIdUseCase: GetFoodsByMenuIdUseCase,
    private val getMenusUseCase: GetMenusUseCase,
    private val getCartSizeUseCase: GetCartSizeUseCase,


    ) : ViewModel() {


    private val _uiState = MutableStateFlow(Home.UiState())
    val uiState: StateFlow<Home.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<Home.Event>()
    val event = _event.receiveAsFlow()


    init {
        getCartSize()
    }

    fun onAction(action: Home.Action) {
        when (action) {
            is Home.Action.OnMenuClicked -> {
                _uiState.update { it.copy(menuIdSelected = action.menuId) }
            }

            is Home.Action.OnFoodClicked -> {
                viewModelScope.launch {
                    _event.send(Home.Event.GoToDetails(action.food))
                }
            }

            is Home.Action.OnChatBoxClicked -> {
                viewModelScope.launch {
                    _event.send(Home.Event.ShowChatBox)
                }
            }

            is Home.Action.OnCartClicked -> {
                viewModelScope.launch {
                    _event.send(Home.Event.GoToCart)
                }
            }

        }
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
}

object Home {
    data class UiState(
        val menuIdSelected: Long = 1,
        val cartSize: Int = 0,
        val error: String? = null,
    )

    sealed interface Event {
        data object ShowError : Event
        data class GoToDetails(val food: Food) : Event
        data object ShowChatBox : Event
        data object GoToCart : Event
    }

    sealed interface Action {
        data class OnMenuClicked(val menuId: Long) : Action
        data class OnFoodClicked(val food: Food) : Action
        data object OnChatBoxClicked : Action
        data object OnCartClicked : Action

    }
}