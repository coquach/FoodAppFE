package com.se114.foodapp.ui.screen.staff.staff_details

import android.net.Uri

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.foodapp.data.dto.ApiResponse

import com.example.foodapp.data.model.ImageInfo

import com.example.foodapp.data.model.Staff
import com.example.foodapp.navigation.EmployeeDetails
import com.example.foodapp.navigation.staffNavType
import com.example.foodapp.ui.screen.components.validateField

import com.se114.foodapp.domain.use_case.staff.CreateStaffUseCase
import com.se114.foodapp.domain.use_case.staff.UpdateStaffUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel

import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class StaffDetailsViewModel @Inject constructor(
    private val createStaffUseCase: CreateStaffUseCase,
    private val updateStaffUseCase: UpdateStaffUseCase,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val arguments = savedStateHandle.toRoute<EmployeeDetails>(
        typeMap = mapOf(typeOf<Staff>() to staffNavType)
    )

    private val mode = arguments.isUpdating
    private val staff = arguments.staff


    private val _uiState = MutableStateFlow(StaffDetails.UiState(isUpdating = mode, staff = staff))
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<StaffDetails.Event>()
    val event get() = _event.receiveAsFlow()


    private fun addStaff() {

        viewModelScope.launch {

            createStaffUseCase.invoke(_uiState.value.staff).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(StaffDetails.Event.BackToListAfterModify)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.errorMessage
                            )
                        }
                        _event.send(StaffDetails.Event.ShowErrorMessage)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }

            }
        }
    }

    private fun updateStaff() {

        viewModelScope.launch {
            updateStaffUseCase.invoke(_uiState.value.staff).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(StaffDetails.Event.BackToListAfterModify)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = response.errorMessage
                            )
                        }
                        _event.send(StaffDetails.Event.ShowErrorMessage)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun validate(type: String) {
        val current = _uiState.value
        var fullNameError: String? = current.nameError
        var phoneError: String? = current.priceError
        var addressError: String? = current.descriptionError
        var basicSalaryError: String? = current.defaultQuantityError
        when (type) {
            "name" -> {
                nameError = validateField(
                    current.foodAddUi.name.trim(),
                    "Tên không hợp lệ"
                ) { it.matches(Regex("^[\\\\p{L}][\\\\p{L} .'-]{1,39}\$")) }


            }

            "price" -> {
                priceError = validateField(
                    current.foodAddUi.price.toPlainString().trim(),
                    "Giá phải lớn hơn 0"
                ) { it.toBigDecimal() > BigDecimal.ZERO } }

            "defaultQuantity" -> {
                defaultQuantityError = validateField(
                    current.foodAddUi.defaultQuantity.toString().trim(),
                    "Số lượng phải lớn hơn 0"
                ) { it.toInt() > 0 }}

            "description" -> {
                descriptionError = validateField(
                    current.foodAddUi.description.trim(),
                    "Mô tả không hợp lệ"

                ) { it.matches(Regex("^[\\\\p{L}][\\\\p{L} .'-]{1,39}\$")) }
            }

        }
        val isValid = current.foodAddUi.name.isNotBlank() && current.foodAddUi.price > BigDecimal.ZERO && current.foodAddUi.defaultQuantity > 0 && current.foodAddUi.description.isNotBlank()
        _uiState.update {
            it.copy(
                nameError = nameError,
                priceError = priceError,
                descriptionError = descriptionError,
                defaultQuantityError = defaultQuantityError,
                isValid = isValid
            )
        }
    }

    fun onAction(action: StaffDetails.Action) {
        when (action) {
            is StaffDetails.Action.AddStaff -> {
                addStaff()
            }

            is StaffDetails.Action.UpdateStaff -> {
                updateStaff()
            }

            StaffDetails.Action.GoBack -> {
                viewModelScope.launch {
                    _event.send(StaffDetails.Event.GoBack)
                }
            }

            is StaffDetails.Action.OnChangeAddress -> {
                _uiState.update { it.copy(staff = it.staff.copy(address = action.value)) }
            }

            is StaffDetails.Action.OnChangeBasicSalary -> {
                _uiState.update {
                    it.copy(
                        staff = it.staff.copy(
                            basicSalary = action.value ?: BigDecimal.ZERO
                        )
                    )
                }
            }

            is StaffDetails.Action.OnChangeBirthDate -> {
                _uiState.update { it.copy(staff = it.staff.copy(birthDate = action.value)) }
            }


            is StaffDetails.Action.OnChangeFullName -> {
                _uiState.update { it.copy(staff = it.staff.copy(fullName = action.value)) }
            }

            is StaffDetails.Action.OnChangeGender -> {
                _uiState.update { it.copy(staff = it.staff.copy(gender = action.value)) }
            }

            is StaffDetails.Action.OnChangeAvatar -> {
                _uiState.update {
                    it.copy(
                        staff = it.staff.copy(
                            avatar = ImageInfo(
                                url = action.value.toString(),
                            )
                        )
                    )
                }
            }

            is StaffDetails.Action.OnChangePhone -> {
                _uiState.update { it.copy(staff = it.staff.copy(phone = action.value)) }
            }

            is StaffDetails.Action.OnChangePosition -> {
                _uiState.update { it.copy(staff = it.staff.copy(position = action.value)) }
            }

            is StaffDetails.Action.OnChangeStartDate -> {
                _uiState.update { it.copy(staff = it.staff.copy(startDate = action.value)) }
            }
        }
    }


}

object StaffDetails {
    data class UiState(
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val staff: Staff,
        val isUpdating: Boolean = false,
        val isValid: Boolean = false,
        val fullNameError: String? = null,
        val phoneError: String? = null,
        val addressError: String? = null,
        val basicSalaryError: String? = null,
    )

    sealed interface Event {
        data object ShowErrorMessage : Event
        data object GoBack : Event
        data object BackToListAfterModify : Event

    }

    sealed interface Action {
        data class OnChangeFullName(val value: String) : Action
        data class OnChangePosition(val value: String) : Action
        data class OnChangePhone(val value: String) : Action
        data class OnChangeGender(val value: String) : Action
        data class OnChangeAddress(val value: String) : Action
        data class OnChangeAvatar(val value: Uri?) : Action
        data class OnChangeBirthDate(val value: LocalDate?) : Action
        data class OnChangeStartDate(val value: LocalDate?) : Action
        data class OnChangeBasicSalary(val value: BigDecimal?) : Action
        data object AddStaff : Action
        data object UpdateStaff : Action
        data object GoBack : Action

    }
}