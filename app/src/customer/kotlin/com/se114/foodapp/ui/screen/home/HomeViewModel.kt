package com.se114.foodapp.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Account
import com.example.foodapp.data.model.Food
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.se114.foodapp.domain.use_case.user.LoadProfileUseCase
import com.se114.foodapp.domain.use_case.ai.GetSuggestFoodsUseCase
import com.se114.foodapp.domain.use_case.cart.GetCartSizeUseCase
import com.se114.foodapp.ui.screen.setting.Setting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSuggestFoodsUseCase: GetSuggestFoodsUseCase,
    private val getCartSizeUseCase: GetCartSizeUseCase,
    private val loadProfileUseCase: LoadProfileUseCase,


    ) : ViewModel() {


    private val _uiState = MutableStateFlow(Home.UiState())
    val uiState: StateFlow<Home.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<Home.Event>()
    val event = _event.receiveAsFlow()

    init {
        getCartSize()
    }

    fun getProfile() {
        viewModelScope.launch {
            loadProfileUseCase().collect { result ->
                when (result) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is FirebaseResult.Success -> {
                        _uiState.update { it.copy(profile = result.data, isLoading = false) }
                    }

                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(error = result.error, isLoading = false) }
                        _event.send(Home.Event.ShowError)
                    }
                }
            }
        }
    }

    fun getGreetingTitle(fullName: String): String {
        val hour = LocalTime.now().hour
        val name = fullName.substringAfterLast(" ").trim()
        Log.d("name account", fullName)
        val greeting = when (hour) {
            in 6..11 -> "Bữa sáng ngon lành đang chờ 🍳"
            in 12..16 -> "Đến giờ cơm trưa rồi 🍱"
            in 17..21 -> "Ăn tối thôi nào 🍜"
            else -> "Đặt món nhẹ nhàng trước khi ngủ 💤"
        }

        return "Chào $name! $greeting"
    }

    fun getFoodSuggestions() {
        viewModelScope.launch {
            getSuggestFoodsUseCase.invoke().collect { response ->
                when (response) {
                    is com.example.foodapp.data.dto.ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                foodSuggestions = response.data,
                                foodSuggestionsState = Home.FoodSuggestions.Success
                            )
                        }
                    }

                    is com.example.foodapp.data.dto.ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                foodSuggestionsState = Home.FoodSuggestions.Error(response.errorMessage)
                            )
                        }
                    }

                    is com.example.foodapp.data.dto.ApiResponse.Loading -> {
                        _uiState.update {
                            it.copy(
                                foodSuggestionsState = Home.FoodSuggestions.Loading
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: Home.Action) {
        when (action) {


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
        val isLoading: Boolean = false,
        val profile: Account = Account(),
        val cartSize: Int = 0,
        val error: String? = null,
        val foodSuggestions: List<Food> = emptyList(),
        val foodSuggestionsState: FoodSuggestions = FoodSuggestions.Loading,
    )

    sealed interface FoodSuggestions {
        data object Loading : FoodSuggestions
        data object Success : FoodSuggestions
        data class Error(val message: String) : FoodSuggestions
    }

    sealed interface Event {
        data object ShowError : Event
        data class GoToDetails(val food: Food) : Event
        data object ShowChatBox : Event
        data object GoToCart : Event
    }

    sealed interface Action {
        data class OnFoodClicked(val food: Food) : Action
        data object OnChatBoxClicked : Action
        data object OnCartClicked : Action


    }
}