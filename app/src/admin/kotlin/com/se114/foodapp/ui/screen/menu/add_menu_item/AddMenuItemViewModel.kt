package com.se114.foodapp.ui.screen.menu.add_menu_item

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.enums.Gender
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.ImageUtils

import com.se114.foodapp.data.dto.request.FoodMultipartRequest

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AddFoodViewModel @Inject constructor(
    private val foodApi: FoodApi,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddFoodState>(AddFoodState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddFoodEvent>()
    val event = _event.asSharedFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _menuId = MutableStateFlow<Long?>(null)

    private val _menuName = MutableStateFlow<String?>(null)
    val menuName = _menuName.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _price = MutableStateFlow<BigDecimal>(BigDecimal(0))
    val price = _price.asStateFlow()

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl = _imageUrl.asStateFlow()




    private val _menusAvailable = MutableStateFlow<List<Menu>>(emptyList())
    val menusAvailable = _menusAvailable.asStateFlow()

    private var isUpdating by mutableStateOf(false)

    init {
        loadMenusAvailable()
    }

    fun setMode(mode: Boolean, item: Food?) {
        isUpdating = mode

        if (isUpdating && item != null) {
            loadFood(item)
        }
    }


    private fun loadMenusAvailable() {
        viewModelScope.launch {
            _uiState.value = AddFoodState.Loading
            try {
                val response = safeApiCall { foodApi.getAvailableMenus() }
                when (response) {
                    is ApiResponse.Success -> {
                        Log.d("Load menu", "Done")
                        val data = response.body
                        _menusAvailable.value = data!!
                        _uiState.value = AddFoodState.Success
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = AddFoodState.Error
                        _event.emit(AddFoodEvent.ShowErrorMessage("Tải danh sách thực đơn lỗi: ${response.message}"))
                    }

                    else -> {
                        _uiState.value = AddFoodState.Error
                        _event.emit(AddFoodEvent.ShowErrorMessage("Lỗi không xác định"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AddFoodState.Error
                _event.emit(AddFoodEvent.ShowErrorMessage("Lỗi không xác định: ${e.message}"))
            }
        }
    }

    private fun loadFood(item: Food) {
        _menuName.value = item.menuName
        _name.value = item.name
        _description.value = item.description
        _price.value = item.price
        _imageUrl.value = item.imageUrl?.let { Uri.parse(it) }
    }

    fun onNameChange(name: String) {
        _name.value = name
    }
    fun onMenuIdChange(menuId: Long) {
        _menuId.value = menuId
    }
    fun onMenuNameChange(menuName: String) {
        _menuName.value = menuName
    }


    fun onDescriptionChange(description: String) {
        _description.value = description
    }

    fun onPriceChange(price: String) {
        try {

            if (price.isBlank() || price == ".") {
                _price.value = BigDecimal.ZERO
            } else {
                _price.value = BigDecimal(price)
            }
        } catch (_: NumberFormatException) {

        }
    }

    fun onImageUrlChange(imageUrl: Uri?) {
        _imageUrl.value = imageUrl
    }


    fun addFood() {
        viewModelScope.launch {
            _uiState.value = AddFoodState.Loading
            try {
                val imagePart = ImageUtils.getImagePart(context, imageUrl.value)
                val request = FoodMultipartRequest(
                    menuId = _menuId.value.toString(),
                    name = _name.value,
                    description = _description.value,
                    price = _price.value,
                    isAvailable = true
                )
                val partMap = request.toPartMap()

                val response = safeApiCall { foodApi.createFood(partMap, imagePart) }

                when (response) {
                    is ApiResponse.Success -> {
                        Log.d("add Food: ", "Done")
                        _uiState.value = AddFoodState.Success
                        Log.d("Event", "About to emit GoBack event")
                        _event.emit(AddFoodEvent.GoBack)
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = AddFoodState.Error
                        _event.emit(AddFoodEvent.ShowErrorMessage("Tạo món ăn thất bại: ${response.message}"))
                    }

                    else -> {
                        _uiState.value = AddFoodState.Error
                        _event.emit(AddFoodEvent.ShowErrorMessage("Có lỗi xảy ra khi tạo"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                _uiState.value = AddFoodState.Error
                _event.emit(AddFoodEvent.ShowErrorMessage("Có lỗi xảy ra khi tạo: ${e.message}"))
            }


        }
    }

    fun updateFood(FoodId: Long) {

        viewModelScope.launch {
            _uiState.value = AddFoodState.Loading
            try {

                val imagePart = ImageUtils.getImagePart(context, imageUrl.value)


                val request = FoodMultipartRequest(
                    menuId = _menuId.value.toString(),
                    name = _name.value,
                    description = _description.value,
                    price = _price.value,
                    isAvailable = true
                )

                val partMap = request.toPartMap()


                val response = safeApiCall { foodApi.updateFood(FoodId, partMap, imagePart) }


                when (response) {
                    is ApiResponse.Success -> {
                        Log.d("update Food: ", "Done")
                        _uiState.value = AddFoodState.Success
                        _event.emit(AddFoodEvent.GoBack)
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = AddFoodState.Error
                        _event.emit(AddFoodEvent.ShowErrorMessage("Cập nhật món ăn thất bại: ${response.message}"))
                    }

                    else -> {
                        _uiState.value = AddFoodState.Error
                        _event.emit(AddFoodEvent.ShowErrorMessage("Có lỗi xảy ra khi cập nhật"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                _uiState.value = AddFoodState.Error
                _event.emit(AddFoodEvent.ShowErrorMessage("Có lỗi xảy ra khi cập nhật: ${e.message}"))
            }
        }
    }

    sealed class AddFoodState {
        data object Nothing : AddFoodState()
        object Loading : AddFoodState()
        object Success : AddFoodState()
        object Error : AddFoodState()
    }

    sealed class AddFoodEvent {
        data class ShowErrorMessage(val message: String) : AddFoodEvent()
        data object GoBack : AddFoodEvent()
    }
}