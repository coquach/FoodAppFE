package com.se114.foodapp.ui.screen.food_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.model.Food
import com.se114.foodapp.domain.use_case.feedback.GetFeedbacksUseCase
import com.se114.foodapp.domain.use_case.food_details.AddToCartUseCase
import com.se114.foodapp.domain.use_case.food_details.ToggleLikecUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
class FoodDetailsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val addToCartUseCase: AddToCartUseCase,
    private val toggleLikecUseCase: ToggleLikecUseCase,
    private val getFeedbacksUseCase: GetFeedbacksUseCase,
) : ViewModel() {


    private val foodArgument: com.example.foodapp.navigation.FoodDetails =
        savedStateHandle.toRoute()

    private val _uiState = MutableStateFlow(FoodDetails.UiState(food = foodArgument.food))
    val uiState: StateFlow<FoodDetails.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<FoodDetails.Event>()
    val event = _event.receiveAsFlow()



    private fun incrementQuantity() {
        _uiState.update { it.copy(quantity = it.quantity + 1) }
    }

    private fun decrementQuantity() {
        if (_uiState.value.quantity == 1) return
        _uiState.update { it.copy(quantity = it.quantity - 1) }
    }

    private fun addToCart(food: Food) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(2000L)
            try {
                val result = addToCartUseCase(food, _uiState.value.quantity)
                when (result) {
                    AddToCartUseCase.Result.ItemAdded -> {
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        _event.send(FoodDetails.Event.OnAddToCart)
                    }

                    AddToCartUseCase.Result.ItemUpdated -> {
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        _event.send(FoodDetails.Event.OnItemAlreadyInCart)
                    }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                _event.send(FoodDetails.Event.ShowError)
            }
        }
    }


    private fun toggleLike(foodId: Long) {
        viewModelScope.launch {
            try {
                toggleLikecUseCase.invoke(foodId).collect { result ->
                    when (result) {
                        is ApiResponse.Failure -> {
                            _uiState.update { it.copy(error = result.errorMessage) }
                            _event.send(FoodDetails.Event.ShowError)
                        }

                        ApiResponse.Loading -> {

                        }

                        is ApiResponse.Success -> {
                            _uiState.update { it.copy(error = null) }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }


        }
    }

    fun getFeedbacks() : StateFlow<PagingData<Feedback>> {
          return  getFeedbacksUseCase.invoke(_uiState.value.food.id).cachedIn(viewModelScope) .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                PagingData.empty()
            )
    }

    fun onAction(action: FoodDetails.Action) {
        when (action) {
            is FoodDetails.Action.OnAddToCart -> {
                addToCart(_uiState.value.food)
            }

            is FoodDetails.Action.GoToCart -> {
                viewModelScope.launch {
                    _event.send(FoodDetails.Event.GoToCart)
                    _uiState.update { it.copy(quantity = 1) }
                }
            }

            is FoodDetails.Action.IncreaseQuantity -> {
                incrementQuantity()
            }

            is FoodDetails.Action.DecreaseQuantity -> {
                decrementQuantity()
            }

            is FoodDetails.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(FoodDetails.Event.OnBack)
                }
            }

            is FoodDetails.Action.OnFavorite -> {
                toggleLike(action.foodId)
            }

        }
    }
}

object FoodDetails {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val food: Food = Food.sample(),
        val quantity: Int = 1,
    )

    sealed interface Event {
        data object ShowError : Event
        data object OnAddToCart : Event
        data object OnItemAlreadyInCart : Event
        data object GoToCart : Event
        data object OnBack : Event

    }

    sealed interface Action {
        data object OnAddToCart : Action
        data object GoToCart : Action
        data object IncreaseQuantity : Action
        data object DecreaseQuantity : Action
        data object OnBack : Action
        data class OnFavorite(val foodId: Long) : Action

    }
}