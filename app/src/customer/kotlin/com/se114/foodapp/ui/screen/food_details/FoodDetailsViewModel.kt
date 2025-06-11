package com.se114.foodapp.ui.screen.food_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.model.Food
import com.example.foodapp.navigation.FoodNavType
import com.se114.foodapp.domain.use_case.feedback.GetFeedbacksUseCase
import com.se114.foodapp.domain.use_case.cart.AddToCartUseCase
import com.se114.foodapp.domain.use_case.food_details.ToggleLikecUseCase
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
import kotlin.reflect.typeOf

@HiltViewModel
class FoodDetailsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val addToCartUseCase: AddToCartUseCase,
    private val toggleLikecUseCase: ToggleLikecUseCase,
    private val getFeedbacksUseCase: GetFeedbacksUseCase,
) : ViewModel() {


    private val foodArgument = savedStateHandle.toRoute<com.example.foodapp.navigation.FoodDetails>(
        typeMap = mapOf(typeOf<Food>() to FoodNavType)
    )

    private val _uiState = MutableStateFlow(FoodDetails.UiState(food = foodArgument.food))
    val uiState: StateFlow<FoodDetails.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<FoodDetails.Event>()
    val event = _event.receiveAsFlow()

    private val _feedbacks = MutableStateFlow<PagingData<Feedback>>(PagingData.empty())
    val feedbacks = _feedbacks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    )


    init{
        getFeedbacks()
    }


    private fun addToCart(food: Food) {
        viewModelScope.launch {


               addToCartUseCase(food, _uiState.value.quantity).collect{result ->
                   when (result) {
                       AddToCartUseCase.Result.ItemAdded -> {
                           _uiState.update { it.copy(isLoading = false, error = null) }
                           _event.send(FoodDetails.Event.OnAddToCart)
                       }

                       AddToCartUseCase.Result.ItemUpdated -> {
                           _uiState.update { it.copy(isLoading = false, error = null) }
                           _event.send(FoodDetails.Event.OnItemAlreadyInCart)
                       }
                       AddToCartUseCase.Result.Loading -> {
                           _uiState.update { it.copy(isLoading = true, error = null) }
                       }
                       is AddToCartUseCase.Result.Failure -> {
                           _uiState.update { it.copy(isLoading = false, error = result.errorMessage) }
                           _event.send(FoodDetails.Event.ShowError)
                       }
                   }
               }



        }
    }


    private fun toggleLike(foodId: Long) {
        Log.d("toggleLike", "ok")
        viewModelScope.launch {
                toggleLikecUseCase.invoke(foodId).collect {result ->
                    when (result) {
                        is ApiResponse.Failure -> {
                            _uiState.update { it.copy(error = result.errorMessage) }
                            _event.send(FoodDetails.Event.ShowError)
                        }

                        is ApiResponse.Success -> {
                            _uiState.update { it.copy(error = null) }
                        }

                        ApiResponse.Loading -> {

                        }
                    }
                }




        }
    }

    private fun getFeedbacks() {
        viewModelScope.launch {
            getFeedbacksUseCase(foodArgument.food.id).cachedIn(viewModelScope).collect {
                _feedbacks.value = it
            }
        }
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


            is FoodDetails.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(FoodDetails.Event.OnBack)
                }
            }

            is FoodDetails.Action.OnFavorite -> {
                toggleLike(action.foodId)
            }

            is FoodDetails.Action.OnChangeQuantity -> {
                _uiState.update { it.copy(quantity =
                        if(action.quantity < 1) 1
                    else if(action.quantity > it.food.remainingQuantity) it.food.remainingQuantity
                    else action.quantity
                ) }
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
       data class OnChangeQuantity(val quantity: Int) : Action
        data object OnBack : Action
        data class OnFavorite(val foodId: Long) : Action

    }
}