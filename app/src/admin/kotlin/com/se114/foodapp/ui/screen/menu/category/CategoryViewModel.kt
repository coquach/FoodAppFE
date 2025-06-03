package com.se114.foodapp.ui.screen.menu.category

import android.util.Log
import androidx.lifecycle.ViewModel

import com.example.foodapp.data.model.Menu
import com.example.foodapp.domain.use_case.food.GetMenusUseCase
import com.se114.foodapp.domain.use_case.menu.AddMenuUseCase
import com.se114.foodapp.domain.use_case.menu.UpdateMenuUseCase
import com.se114.foodapp.domain.use_case.menu.UpdateStatusMenuUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

import javax.inject.Inject


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getMenusUseCase: GetMenusUseCase,
    private val addMenuUseCase: AddMenuUseCase,
    private val updateMenuUseCase: UpdateMenuUseCase,
    private val updateStatusMenuUseCase: UpdateStatusMenuUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryState.UiState())
    val uiState: StateFlow<CategoryState.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<CategoryState.Event>()
    val event = _event.receiveAsFlow()



//    private fun loadCategories() {
//        viewModelScope.launch {
//            _uiState.value = CategoryState.Loading
//            try {
//                val response = safeApiCall { foodApi.getAvailableMenus() }
//                when (response) {
//                    is ApiResponse.Success -> {
//                        Log.d("Load menu", "Done")
//                        val data = response.body
//                        _categoryList.value = data ?: emptyList()
//                        _uiState.value = CategoryState.Success
//                    }
//
//                    is ApiResponse.Error -> {
//                        _uiState.value = CategoryState.Error
//                        _event.emit(CategoryEvents.ShowErrorMessage("Tải danh sách thực đơn lỗi: ${response.message}"))
//                    }
//
//                    else -> {
//                        _uiState.value = CategoryState.Error
//                        _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định"))
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _uiState.value = CategoryState.Error
//                _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định: ${e.message}"))
//            }
//        }
//    }
//
//    fun addCategory() {
//        viewModelScope.launch {
//            _uiState.value = CategoryState.Loading
//            try {
//                val request = MenuRequest(
//                    name = _categoryName.value
//                )
//
//                val response = safeApiCall { foodApi.createMenu(request) }
//                when (response) {
//                    is ApiResponse.Success -> {
//                        Log.d("Add menu", "Done")
//                        val newCategory = response.body
//                        _categoryList.update { oldList ->
//                            oldList + newCategory!!
//                        }
//                        _categoryName.value = ""
//                        _uiState.value = CategoryState.Success
//                        loadCategories()
//                    }
//                    is ApiResponse.Error -> {
//                        _uiState.value = CategoryState.Error
//                        _event.emit(CategoryEvents.ShowErrorMessage("Thêm danh mục thất bại"))
//                        Log.d("Add menu", response.message)
//                    }
//                    else -> {
//                        _uiState.value = CategoryState.Error
//                        _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định khi thêm"))
//
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định"))
//                Log.d("error add menu", e.message.toString())
//            }
//        }
//    }
//    fun updateCategory(menuId: Long) {
//        viewModelScope.launch {
//            _uiState.value = CategoryState.Loading
//            try {
//                val request = MenuRequest(
//                    name = _categoryName.value
//                )
//
//                val response = safeApiCall { foodApi.updateMenu(menuId,request) }
//                when (response) {
//                    is ApiResponse.Success -> {
//                        val updatedCategory = response.body
//
//                            _categoryList.update { oldList ->
//                                oldList.map { (if (it.id == menuId) updatedCategory else it)!! }
//                            }
//                            _categoryName.value = ""
//                        _uiState.value = CategoryState.Success
//                        loadCategories()
//                    }
//                    is ApiResponse.Error -> {
//                        _uiState.value = CategoryState.Error
//                        _event.emit(CategoryEvents.ShowErrorMessage("Sửa danh mục thất bại"))
//                        Log.d("Add menu", response.message)
//                    }
//                    else -> {
//                        _uiState.value = CategoryState.Error
//                        _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định khi sửa"))
//
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định"))
//                Log.d("error add menu", e.message.toString())
//            }
//        }
//    }

    fun onAction(action: CategoryState.Action){
        when(action){
            is CategoryState.Action.OnChangeName -> {
                _uiState.update { it.copy(menuSelected = it.menuSelected.copy(name = action.name)) }
            }
            CategoryState.Action.AddMenu -> {

            }
            is CategoryState.Action.OnSelectMenu -> {
                _uiState.update { it.copy(menuSelected = action.menu) }
            }
            is CategoryState.Action.UpdateMenu -> {

            }

            is CategoryState.Action.UpdateStatus -> {

            }

            is CategoryState.Action.OnChangeStatusUi -> {
                _uiState.update { it.copy(isCreating = action.status) }
            }
        }
    }


}

object CategoryState{
    data class UiState(
        val isLoading: Boolean = false,
        val menuState: MenuState = MenuState.Loading,
        val menuList: List<Menu> = emptyList(),
        val menuSelected: Menu = Menu(),
        val errorMessage: String? = null,
        val isCreating: Boolean = false,
    )
    sealed interface MenuState{
        data object Loading: MenuState
        data object Success: MenuState
        data class Error(val message: String): MenuState

    }
    sealed class Event{
        data object ShowError: Event()
        data object OnBack: Event()
    }
    sealed interface Action{
        data class OnChangeName(val name: String): Action
        data object AddMenu: Action
        data object UpdateMenu: Action
        data class UpdateStatus(val status: Boolean): Action
        data class OnSelectMenu(val menu: Menu): Action
        data class OnChangeStatusUi(val status: Boolean): Action
    }

}