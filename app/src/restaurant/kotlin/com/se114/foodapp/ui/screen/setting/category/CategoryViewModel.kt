package com.se114.foodapp.ui.screen.setting.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.remote.FoodApi
import com.se114.foodapp.data.dto.request.MenuRequest
import com.se114.foodapp.ui.screen.menu.add_menu_item.AddMenuItemViewModel.AddMenuItemEvent
import com.se114.foodapp.ui.screen.menu.add_menu_item.AddMenuItemViewModel.AddMenuItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {
    private val _uiState = MutableStateFlow<CategoryState>(CategoryState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CategoryEvents>()
    val event = _event.asSharedFlow()

    private val _categoryList = MutableStateFlow<List<Menu>>(emptyList())
    val categoryList = _categoryList.asStateFlow()

    private val _categoryName = MutableStateFlow("")
    val categoryName = _categoryName.asStateFlow()

    fun onChangeCategoryName(categoryName: String) {
        _categoryName.value = categoryName
    }

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = CategoryState.Loading
            try {
                val response = safeApiCall { foodApi.getAvailableMenus() }
                when (response) {
                    is ApiResponse.Success -> {
                        Log.d("Load menu", "Done")
                        val data = response.body
                        _categoryList.value = data!!
                        _uiState.value = CategoryState.Success
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = CategoryState.Error
                        _event.emit(CategoryEvents.ShowErrorMessage("Tải danh sách thực đơn lỗi: ${response.message}"))
                    }

                    else -> {
                        _uiState.value = CategoryState.Error
                        _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = CategoryState.Error
                _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định: ${e.message}"))
            }
        }
    }

    fun addCategory() {
        viewModelScope.launch {
            _uiState.value = CategoryState.Loading
            try {
                val request = MenuRequest(
                    name = _categoryName.value
                )

                val response = safeApiCall { foodApi.createMenu(request) }
                when (response) {
                    is ApiResponse.Success -> {
                        Log.d("Add menu", "Done")
                        val newCategory = response.body
                        _categoryList.update { oldList ->
                            oldList + newCategory!!
                        }
                        _categoryName.value = ""
                        _uiState.value = CategoryState.Success
                        loadCategories()
                    }
                    is ApiResponse.Error -> {
                        _uiState.value = CategoryState.Error
                        _event.emit(CategoryEvents.ShowErrorMessage("Thêm danh mục thất bại"))
                        Log.d("Add menu", response.message)
                    }
                    else -> {
                        _uiState.value = CategoryState.Error
                        _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định khi thêm"))

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định"))
                Log.d("error add menu", e.message.toString())
            }
        }
    }
    fun updateCategory(menuId: Long) {
        viewModelScope.launch {
            _uiState.value = CategoryState.Loading
            try {
                val request = MenuRequest(
                    name = _categoryName.value
                )

                val response = safeApiCall { foodApi.updateMenu(menuId,request) }
                when (response) {
                    is ApiResponse.Success -> {
                        val updatedCategory = response.body

                            _categoryList.update { oldList ->
                                oldList.map { (if (it.id == menuId) updatedCategory else it)!! }
                            }
                            _categoryName.value = ""
                        _uiState.value = CategoryState.Success
                        loadCategories()
                    }
                    is ApiResponse.Error -> {
                        _uiState.value = CategoryState.Error
                        _event.emit(CategoryEvents.ShowErrorMessage("Sửa danh mục thất bại"))
                        Log.d("Add menu", response.message)
                    }
                    else -> {
                        _uiState.value = CategoryState.Error
                        _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định khi sửa"))

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _event.emit(CategoryEvents.ShowErrorMessage("Lỗi không xác định"))
                Log.d("error add menu", e.message.toString())
            }
        }
    }


    sealed class CategoryState {
        object Nothing : CategoryState()
        object Loading : CategoryState()
        object Success : CategoryState()
        object Error : CategoryState()
    }

    sealed class CategoryEvents {
        data class ShowErrorMessage(val message: String) : CategoryEvents()
    }
}