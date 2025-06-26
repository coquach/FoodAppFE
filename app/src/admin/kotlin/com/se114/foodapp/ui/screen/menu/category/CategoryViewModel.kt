package com.se114.foodapp.ui.screen.menu.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Menu
import com.example.foodapp.domain.use_case.food.GetMenusUseCase
import com.se114.foodapp.domain.use_case.menu.AddMenuUseCase
import com.se114.foodapp.domain.use_case.menu.UpdateMenuUseCase
import com.se114.foodapp.domain.use_case.menu.UpdateStatusMenuUseCase
import com.se114.foodapp.ui.screen.menu.category.CategoryState.MenuState
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
class CategoryViewModel @Inject constructor(
    private val getMenusUseCase: GetMenusUseCase,
    private val addMenuUseCase: AddMenuUseCase,
    private val updateMenuUseCase: UpdateMenuUseCase,
    private val updateStatusMenuUseCase: UpdateStatusMenuUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryState.UiState())
    val uiState: StateFlow<CategoryState.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<CategoryState.Event>()
    val event get() = _event.receiveAsFlow()


    fun getMenus() {
        viewModelScope.launch {
            getMenusUseCase(
                status = null,
                name = _uiState.value.search
            ).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                menuState = MenuState.Success,
                                menuList = response.data
                            )
                        }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(menuState = MenuState.Error(response.errorMessage)) }

                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(menuState = MenuState.Loading) }
                    }
                }

            }
        }
    }

    private fun createMenu() {
        viewModelScope.launch {
            addMenuUseCase(name = _uiState.value.menuSelected.name).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false, errorMessage = null,
                                menuList = it.menuList + response.data
                            )
                        }
                        _event.send(CategoryState.Event.ShowMessage("Thêm thực đơn thành công"))
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.errorMessage
                            )
                        }
                        _event.send(CategoryState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun updateMenu() {
        viewModelScope.launch {
            updateMenuUseCase(
                menuId = _uiState.value.menuSelected.id!!,
                name = _uiState.value.menuSelected.name
            ).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false, errorMessage = null,
                                menuList = it.menuList.map { menu -> if (menu.id == response.data.id) response.data else menu })
                        }
                        _event.send(CategoryState.Event.ShowMessage("Cập nhật thực đơn thành công"))

                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.errorMessage
                            )
                        }
                        _event.send(CategoryState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun setStatusMenu() {
        viewModelScope.launch {
            updateStatusMenuUseCase(menuId = _uiState.value.menuSelected.id!!).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false, errorMessage = null,
                            )
                        }
                        _event.send(CategoryState.Event.ShowMessage("Cập nhật trạng thái thực đơn thành công"))
                        getMenus()
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.errorMessage
                            )
                        }
                        _event.send(CategoryState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                }
            }
        }
    }


    fun onAction(action: CategoryState.Action) {
        when (action) {
            is CategoryState.Action.OnChangeName -> {
                _uiState.update { it.copy(menuSelected = it.menuSelected.copy(name = action.name)) }
            }

            CategoryState.Action.AddMenu -> {
                createMenu()
            }

            is CategoryState.Action.OnSelectMenu -> {
                _uiState.update { it.copy(menuSelected = action.menu) }
            }

            is CategoryState.Action.UpdateMenu -> {
                updateMenu()
            }

            is CategoryState.Action.UpdateStatus -> {
                setStatusMenu()
            }
            CategoryState.Action.Retry -> {
                getMenus()
            }
            CategoryState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(CategoryState.Event.OnBack)
                }
            }


        }
    }


}


object CategoryState {
    data class UiState(
        val isLoading: Boolean = false,
        val menuState: MenuState = MenuState.Loading,
        val menuList: List<Menu> = emptyList(),
        val menuSelected: Menu = Menu(),
        val errorMessage: String? = null,
        val status: Boolean = true,
        val search: String? = null,
    )

    sealed interface MenuState {
        data object Loading : MenuState
        data object Success : MenuState
        data class Error(val message: String) : MenuState

    }

    sealed class Event {
        data object ShowError : Event()
        data class ShowMessage(val message: String) : Event()
        data object OnBack : Event()
    }

    sealed interface Action {
        data object OnBack : Action
        data class OnChangeName(val name: String) : Action
        data object AddMenu : Action
        data object UpdateMenu : Action
        data object UpdateStatus : Action
        data class OnSelectMenu(val menu: Menu) : Action
        data object Retry : Action
    }

}