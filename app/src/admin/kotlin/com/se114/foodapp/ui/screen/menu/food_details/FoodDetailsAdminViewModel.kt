package com.se114.foodapp.ui.screen.menu.food_details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Menu
import com.example.foodapp.domain.use_case.food.GetMenusUseCase
import com.example.foodapp.navigation.FoodDetailsAdmin
import com.se114.foodapp.data.mapper.toFoodAddUi
import com.se114.foodapp.domain.use_case.food.CreateFoodUseCase
import com.se114.foodapp.domain.use_case.food.UpdateFoodUseCase

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
class FoodDetailsAdminViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val createFoodUseCase: CreateFoodUseCase,
    private val updateFoodUseCase: UpdateFoodUseCase,
    private val getMenusUseCase: GetMenusUseCase,
) : ViewModel() {
    private val foodArgument: FoodDetailsAdmin = savedStateHandle.toRoute()
    private val food = foodArgument.food
    private val isUpdating = foodArgument.isUpdating

    private val _uiState =
        MutableStateFlow(AddFood.UiState(foodAddUi = food.toFoodAddUi(), isUpdating = isUpdating))
    val uiState: StateFlow<AddFood.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<AddFood.Event>()
    val event = _event.receiveAsFlow()

    val menus: StateFlow<PagingData<Menu>> = getMenusUseCase.invoke()
        .cachedIn(viewModelScope).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PagingData.empty()
        )

    private fun createFood() {
        viewModelScope.launch {

            createFoodUseCase.invoke(uiState.value.foodAddUi).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                            )
                        }
                        _event.send(AddFood.Event.OnBackAfterUpdate)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Thêm món ăn thất bại: ${result.errorMessage}"
                            )
                        }
                        _event.send(AddFood.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }

            }

        }
    }

    private fun updateFood() {

        viewModelScope.launch {
            updateFoodUseCase(uiState.value.foodAddUi).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                            )
                        }
                        _event.send(AddFood.Event.OnBackAfterUpdate)

                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Cập nhật món ăn thất bại: ${result.errorMessage}"
                            )
                        }
                        _event.send(AddFood.Event.ShowError)
                    }
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }

        }
    }

    fun onAction(action: AddFood.Action) {
        when (action) {
            is AddFood.Action.AddFood -> {
                createFood()
            }
            is AddFood.Action.UpdateFood -> {
                updateFood()
            }
            is AddFood.Action.OnNameChange -> {
                _uiState.update { it.copy(foodAddUi = it.foodAddUi.copy(name = action.name)) }
            }
            is AddFood.Action.OnMenuIdChange -> {
                _uiState.update { it.copy(foodAddUi = it.foodAddUi.copy(menuId = action.menuId)) }
            }
            is AddFood.Action.OnDescriptionChange -> {
                _uiState.update { it.copy(foodAddUi = it.foodAddUi.copy(description = action.description)) }
            }
            is AddFood.Action.OnPriceChange -> {
                _uiState.update { it.copy(foodAddUi = it.foodAddUi.copy(price = action.price?: BigDecimal.ZERO)) }
            }

            is AddFood.Action.OnDefaultQuantityChange -> {
                _uiState.update { it.copy(foodAddUi = it.foodAddUi.copy(defaultQuantity = action.defaultQuantity?: 0)) }
            }
            is AddFood.Action.OnImagesChange -> {
                _uiState.update { it.copy(foodAddUi = it.foodAddUi.copy(images = action.images)) }
            }
            is AddFood.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(AddFood.Event.OnBack)
                }
            }
        }
    }
}




object AddFood {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val foodAddUi: FoodAddUi,
        val isUpdating: Boolean = false,
    )

    sealed interface Event {
        data object OnBack : Event
        data object OnBackAfterUpdate : Event
        data object ShowError : Event
    }

    sealed interface Action {
        data object AddFood : Action
        data object UpdateFood : Action
        data class OnNameChange(val name: String) : Action
        data class OnMenuIdChange(val menuId: Long) : Action
        data class OnDescriptionChange(val description: String) : Action
        data class OnPriceChange(val price: BigDecimal?) : Action
        data class OnDefaultQuantityChange(val defaultQuantity: Int?) : Action
        data class OnImagesChange(val images: List<Uri>?) : Action
        data object OnBack : Action

    }
}

data class FoodAddUi(
    val id: Long ?= null,
    val name: String = "",
    val menuId: Long = 1,
    val description: String = "",
    val images: List<Uri>? = emptyList(),
    val price: BigDecimal = BigDecimal.ZERO,
    val defaultQuantity: Int = 1,
)