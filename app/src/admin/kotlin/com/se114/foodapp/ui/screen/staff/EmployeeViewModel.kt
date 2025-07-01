package com.se114.foodapp.ui.screen.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.data.model.Staff
import com.example.foodapp.domain.use_case.staff.GetStaffUseCase
import com.se114.foodapp.domain.use_case.staff.DeleteStaffUseCase
import com.se114.foodapp.domain.use_case.staff.TerminateStaffUseCase
import com.se114.foodapp.ui.screen.staff.EmployeeSate.Event.GoToDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class EmployeeViewModel @Inject constructor(
    private val getStaffUseCase: GetStaffUseCase,
    private val deleteStaffUseCase: DeleteStaffUseCase,
    private val terminateStaffUseCase: TerminateStaffUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        EmployeeSate.UiState(
            staffFilter = StaffFilter(active = true)
        )
    )
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<EmployeeSate.Event>()
    val event get() = _event.receiveAsFlow()

    fun getStaffs(filter: StaffFilter) = getStaffUseCase(filter)

    private fun deleteStaff() {
        viewModelScope.launch {
            deleteStaffUseCase.invoke(_uiState.value.staffSelected!!.id!!).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(EmployeeSate.Event.ShowSuccessToast("Xóa nhân viên thành công"))
                        onAction(EmployeeSate.Action.OnRefresh)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(EmployeeSate.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }

        }

    }

    private fun terminateStaff() {
        viewModelScope.launch {
            terminateStaffUseCase(_uiState.value.staffSelected!!.id!!).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(EmployeeSate.Event.ShowSuccessToast("Ngưng việc nhân viên thành công"))
                        onAction(EmployeeSate.Action.OnRefresh)
                    }
                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage)}
                    _event.send(EmployeeSate.Event.ShowError)
                    }
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onAction(action: EmployeeSate.Action) {
        when (action) {
            is EmployeeSate.Action.OnDeleteStaff -> {
                deleteStaff()
            }

            is EmployeeSate.Action.OnRefresh -> {
                _uiState.update {
                    it.copy(
                        staffFilter = it.staffFilter.copy(forceRefresh = UUID.randomUUID().toString())
                    )
                }
            }

            is EmployeeSate.Action.OnStaffSelected -> {
                _uiState.update { it.copy(staffSelected = action.staff) }
            }

            is EmployeeSate.Action.OnChangeStatusFilter -> {
                _uiState.update {
                    it.copy(
                        staffFilter = it.staffFilter.copy(active = action.status)
                    )
                }
            }

            is EmployeeSate.Action.OnStaffClicked -> {
                viewModelScope.launch {
                    _event.send(GoToDetail(action.staff))
                }
            }

            EmployeeSate.Action.OnAddStaff -> {
                viewModelScope.launch {
                    _event.send(EmployeeSate.Event.GoToAddStaff)
                }
            }

            is EmployeeSate.Action.OnSortByChange -> {
                _uiState.update {
                    it.copy(
                        staffFilter = it.staffFilter.copy(sortBy = action.sortBy)
                    )
                }
            }

            is EmployeeSate.Action.OnOrderChange -> {
                _uiState.update {
                    it.copy(
                        staffFilter = it.staffFilter.copy(order = action.order)
                    )
                }
            }

            is EmployeeSate.Action.OnNameSearchChange -> {
                _uiState.update {
                    it.copy(
                        nameSearch = action.nameSearch
                    )
                }
            }

            EmployeeSate.Action.OnSearchFilter -> {
                _uiState.update {
                    it.copy(
                        staffFilter = it.staffFilter.copy(fullName = _uiState.value.nameSearch)
                    )
                }
            }
            EmployeeSate.Action.OnTerminateStaff -> {
                terminateStaff()
            }
        }
    }
}

object EmployeeSate {
    data class UiState(
        val staffFilter: StaffFilter = StaffFilter(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val staffSelected: Staff? = null,
        val nameSearch: String = "",
    )

    sealed interface Event {
        data object ShowError : Event
        data class GoToDetail(val staff: Staff) : Event
        data object GoToAddStaff : Event
        data class ShowSuccessToast(val message: String) : Event

    }

    sealed interface Action {
        data class OnNameSearchChange(val nameSearch: String) : Action
        data object OnSearchFilter : Action
        data class OnOrderChange(val order: String) : Action
        data class OnSortByChange(val sortBy: String) : Action
        data class OnStaffClicked(val staff: Staff) : Action
        data class OnChangeStatusFilter(val status: Boolean) : Action
        data class OnStaffSelected(val staff: Staff) : Action
        data object OnDeleteStaff : Action
        data object OnRefresh : Action
        data object OnAddStaff : Action
        data object OnTerminateStaff : Action
    }
}