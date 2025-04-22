package com.se114.foodapp.ui.screen.menu.add_menu_item

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.data.remote.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMenuItemViewModel @Inject constructor(
    private val foodApi: FoodApi,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl = _imageUrl.asStateFlow()


    private val _addMenuItemState = MutableStateFlow<AddMenuItemState>(AddMenuItemState.Nothing)
    val addMenuItemState = _addMenuItemState.asStateFlow()

    private val _addMenuItemEvent = MutableSharedFlow<AddMenuItemEvent>()
    val addMenuItemEvent = _addMenuItemEvent.asSharedFlow()

    private var isUpdating by mutableStateOf(false)

    fun setMode(mode: Boolean, item: FoodItem?) {
        isUpdating = mode

        if (isUpdating && item != null) {
            loadMenuItem(item)
        }
    }
    private fun loadMenuItem(item: FoodItem) {
        _name.value = item.name
        _description.value = item.description
        _price.value = item.price.toString()
        _imageUrl.value = item.imageUrl?.let { Uri.parse(it) }
    }

    fun onNameChange(name: String) {
        _name.value = name
    }

    fun onDescriptionChange(description: String) {
        _description.value = description
    }

    fun onPriceChange(price: String) {
        _price.value = price
    }

    fun onImageUrlChange(imageUrl: Uri?) {
        _imageUrl.value = imageUrl
    }
    fun addMenuItem() {
        val name = name.value
        val description = description.value
        val price = price.value.toFloatOrNull() ?: 0.0f

        if (name.isEmpty() || description.isEmpty() || price == 0.0f || imageUrl.value == null) {
            _addMenuItemEvent.tryEmit(AddMenuItemEvent.ShowErrorMessage("Please fill all fields"))
            return
        }
        viewModelScope.launch {
//            _addMenuItemState.value = AddMenuItemState.Loading
//            val imageUrl = uploadImage(imageUri = imageUrl.value!!)
//            if (imageUrl == null) {
//                _addMenuItemState.value = AddMenuItemState.Error("Failed to upload image")
//                return@launch
//            }
//            val response = safeApiCall {
//                foodApi.addRestaurantMenu(
//                    restaurantId,
//                    FoodItem(
//                        name = name,
//                        description = description,
//                        price = price,
//                        imageUrl = imageUrl,
//                        restaurantId = restaurantId
//                    )
//                )
//            }
//            when (response) {
//                is ApiResponse.Success -> {
//                    _addMenuItemState.value = AddMenuItemState.Success("Item added successfully")
//                    _addMenuItemEvent.emit(AddMenuItemEvent.GoBack)
//                }
//
//                is ApiResponse.Error -> {
//                    _addMenuItemState.value = AddMenuItemState.Error(response.message)
//                }
//
//                is ApiResponse.Exception -> {
//                    _addMenuItemState.value = AddMenuItemState.Error("Network Error")
//                }
//            }
        }
    }

//    suspend fun uploadImage(imageUri: Uri): String? {
//        val file = ImageUtils.getFileFromUri(context, imageUri)
//        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//        val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
//        val response = safeApiCall { foodApi.uploadImage(multipartBody) }
//        when (response) {
//            is ApiResponse.Success -> {
//                return response.data.url
//            }
//
//            else -> {
//                return null
//            }
//        }
//    }


    sealed class AddMenuItemState {
        object Nothing : AddMenuItemState()
        object Loading : AddMenuItemState()
        data class Success(val message: String) : AddMenuItemState()
        data class Error(val message: String) : AddMenuItemState()
    }

    sealed class AddMenuItemEvent {
        data class ShowErrorMessage(val message: String) : AddMenuItemEvent()
        object GoBack : AddMenuItemEvent()
    }
}