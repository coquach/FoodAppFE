package com.se114.foodapp.ui.screen.employee.add_employee

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.enums.Gender
import com.se114.foodapp.data.model.Staff

import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.ImageUtils.getFileFromUri
import com.example.foodapp.utils.ImageUtils.toMultipartBodyPart
import com.se114.foodapp.data.dto.request.StaffMultipartRequest
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

    private val _position = MutableStateFlow<String?>(null)
    val position = _position.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _gender = MutableStateFlow<String>("")
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


    private val _uiState = MutableStateFlow<AddEmployeeState>(AddEmployeeState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<AddEmployeeEvents>()
    val event = _event.asSharedFlow()

    private var isUpdating by mutableStateOf(false)

    fun setMode(mode: Boolean, item: Staff?) {
        isUpdating = mode

        if (isUpdating && item != null) {
            loadStaffItem(item)
        }
    }

    private fun loadStaffItem(item: Staff) {
        _fullName.value = item.fullName ?: ""
        _position.value = item.position ?: ""
        _phone.value = item.phone ?: ""
        _gender.value = Gender.fromName(item.gender!!)?.display ?: ""
        _address.value = item.address ?: ""
        _imageUrl.value = item.imageUrl?.let { Uri.parse(it) }
        _birthDate.value = item.birthDate
        _startDate.value = item.startDate
        _endDate.value = item.endDate
        _basicSalary.value = item.basicSalary
    }

    fun onFullNameChange(value: String) {
        _fullName.value = value
    }

    fun onPositionChange(value: String?) {
        _position.value = value
    }

    fun onPhoneChange(value: String) {
        _phone.value = value
    }

    fun onGenderChange(value: String) {
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

    fun addStaff() {

        viewModelScope.launch {
            _uiState.value = AddEmployeeState.Loading
            try {
                val imageFile = _imageUrl.value?.let { getFileFromUri(context, it) }
                val imagePart = imageFile?.toMultipartBodyPart()
                val request = StaffMultipartRequest(
                    fullName = _fullName.value,
                    position = _position.value!!,
                    phone = _phone.value,
                    gender = Gender.fromDisplay(gender.value)!!.name,
                    address = _address.value,
                    birthDate = _birthDate.value.toString(),
                    startDate = _startDate.value.toString(),
                    endDate = _endDate.value.toString(),
                    basicSalary = _basicSalary.value
                )
                val partMap = request.toPartMap()

                val response = safeApiCall { foodApi.createStaff(partMap, imagePart) }

                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.value = AddEmployeeState.Success
                        _event.emit(AddEmployeeEvents.GoBack)
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = AddEmployeeState.Error
                        _event.emit(AddEmployeeEvents.ShowErrorMessage("Tạo nhân viên thất bại: ${response.message}"))
                    }

                    else -> {
                        _uiState.value = AddEmployeeState.Error
                        _event.emit(AddEmployeeEvents.ShowErrorMessage("Có lỗi xảy ra khi tạo"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                _uiState.value = AddEmployeeState.Error
                _event.emit(AddEmployeeEvents.ShowErrorMessage("Có lỗi xảy ra khi tạo: ${e.message}"))
            }


        }
    }

    fun updateStaff(staffId: Long) {

        viewModelScope.launch {
            _uiState.value = AddEmployeeState.Loading
            try {

                val imageFile = _imageUrl.value?.let { getFileFromUri(context, it) }
                val imagePart = imageFile?.toMultipartBodyPart()


                val request = StaffMultipartRequest(
                    fullName = _fullName.value,
                    position = _position.value!!,
                    phone = _phone.value,
                    gender = Gender.fromDisplay(gender.value)!!.name,
                    address = _address.value,
                    birthDate = _birthDate.value.toString(),
                    startDate = _startDate.value.toString(),
                    endDate = _endDate.value.toString(),
                    basicSalary = _basicSalary.value
                )

                val partMap = request.toPartMap()


                val response = safeApiCall { foodApi.updateStaff(staffId, partMap, imagePart) }


                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.value = AddEmployeeState.Success
                        _event.emit(AddEmployeeEvents.GoBack)
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = AddEmployeeState.Error
                        _event.emit(AddEmployeeEvents.ShowErrorMessage("Cập nhật nhân viên thất bại: ${response.message}"))
                    }

                    else -> {
                        _uiState.value = AddEmployeeState.Error
                        _event.emit(AddEmployeeEvents.ShowErrorMessage("Có lỗi xảy ra khi cập nhật"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                _uiState.value = AddEmployeeState.Error
                _event.emit(AddEmployeeEvents.ShowErrorMessage("Có lỗi xảy ra khi cập nhật: ${e.message}"))
            }
        }
    }




sealed class AddEmployeeState {
    object Nothing : AddEmployeeState()
    object Loading : AddEmployeeState()
    object Success : AddEmployeeState()
    object Error : AddEmployeeState()
}

sealed class AddEmployeeEvents {
    data class ShowErrorMessage(val message: String) : AddEmployeeEvents()
    object GoBack : AddEmployeeEvents()
}
}