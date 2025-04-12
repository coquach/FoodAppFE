package com.se114.foodapp.ui.screen.employee.add_employee

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.data.model.Staff

import com.example.foodapp.data.remote.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEmployeeViewModel @Inject constructor(
    private val foodApi: FoodApi,
    @ApplicationContext val context: Context
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName = _fullName.asStateFlow()

    private val _position = MutableStateFlow("")
    val position = _position.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _gender = MutableStateFlow<String?>(null)
    val gender = _gender.asStateFlow()

    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl = _imageUrl.asStateFlow()

    private val _birthDate = MutableStateFlow<LocalDate?>(null)
    val birthDate = _birthDate.asStateFlow()

    private val _startDate = MutableStateFlow<LocalDate?>(null)
    val startDate = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<LocalDate?>(null)
    val endDate = _endDate.asStateFlow()



    private val _basicSalary = MutableStateFlow(0.0)
    val basicSalary = _basicSalary.asStateFlow()


    private val _addEmployeeState = MutableStateFlow<AddEmployeeState>(AddEmployeeState.Nothing)
    val addEmployeeState = _addEmployeeState.asStateFlow()

    private val _addEmployeeEvent = MutableSharedFlow<AddEmployeeEvents>()
    val addEmployeeEvent = _addEmployeeEvent.asSharedFlow()

    private var isUpdating by mutableStateOf(false)

    fun setMode(mode: Boolean, item: Staff?) {
        isUpdating = mode

        if (isUpdating && item != null) {
            loadStaffItem(item)
        }
    }
    private fun loadStaffItem(item: Staff) {
        _fullName.value = item.fullName?: ""
        _position.value = item.position?: ""
        _phone.value = item.phone?: ""
        _gender.value = item.gender
        _address.value = item.address?: ""
        _imageUrl.value = item.imageUrl?.let { Uri.parse(it) }
        _birthDate.value = item.birthDate
        _startDate.value = item.startDate
        _endDate.value = item.endDate
        _basicSalary.value = item.basicSalary
    }
    fun onFullNameChange(value: String) {
        _fullName.value = value
    }

    fun onPositionChange(value: String) {
        _position.value = value
    }

    fun onPhoneChange(value: String) {
        _phone.value = value
    }

    fun onGenderChange(value: String?) {
        _gender.value = value
    }

    fun onAddressChange(value: String) {
        _address.value = value
    }

    fun onImageUrlChange(uri: Uri?) {
        _imageUrl.value = uri
    }

    fun onBirthDateChange(date: LocalDate?) {
        _birthDate.value = date
    }

    fun onStartDateChange(date: LocalDate?) {
        _startDate.value = date
    }

    fun onEndDateChange(date: LocalDate?) {
        _endDate.value = date
    }

    fun onBasicSalaryChange(value: String) {
        _basicSalary.value = value.toDoubleOrNull() ?: 0.0
    }

    fun addMenuItem() {
//        val name = name.value
//        val description = description.value
//        val price = price.value.toFloatOrNull() ?: 0.0f
//
//        if (name.isEmpty() || description.isEmpty() || price == 0.0f || imageUrl.value == null) {
//            _addEmployeeEvent.tryEmit(AddEmployeeEvents.ShowErrorMessage("Please fill all fields"))
//            return
//        }
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




    sealed class AddEmployeeState {
        object Nothing : AddEmployeeState()
        object Loading : AddEmployeeState()
        data class Success(val message: String) : AddEmployeeState()
        data class Error(val message: String) : AddEmployeeState()
    }

    sealed class AddEmployeeEvents {
        data class ShowErrorMessage(val message: String) : AddEmployeeEvents()
        object GoBack : AddEmployeeEvents()
    }
}