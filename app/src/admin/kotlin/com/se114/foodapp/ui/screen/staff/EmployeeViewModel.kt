package com.se114.foodapp.ui.screen.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.OrderFilter
import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.model.enums.OrderStatus
import com.example.foodapp.domain.use_case.staff.GetStaffUseCase
import com.example.foodapp.utils.TabCacheManager
import com.se114.foodapp.domain.use_case.staff.DeleteStaffUseCase
import com.se114.foodapp.ui.screen.staff.EmployeeSate.Event.GoToDetail
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
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class EmployeeViewModel @Inject constructor(
    private val getStaffUseCase: GetStaffUseCase,
    private val deleteStaffUseCase: DeleteStaffUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeSate.UiState(
        staffFilter = StaffFilter(status = true)
    ))
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<EmployeeSate.Event>()
    val event get() = _event.receiveAsFlow()



    val staffsTabManager = TabCacheManager<Int, Staff>(
        scope = viewModelScope,
        getFilter = { tabIndex ->
            val status = getStaffStatusForTab(tabIndex)
            _uiState.value.staffFilter.copy(status = status)
        },
        loadData = { filter ->
            getStaffUseCase(filter as StaffFilter)
        }
    )

    fun getStaffsFlow(tabIndex: Int){
        return staffsTabManager.getFlowForTab(tabIndex)
    }




    private fun getStaffStatusForTab(tabIndex: Int): Boolean? {
        return when (tabIndex) {
            0 -> true
            1-> false
            else -> null
        }
    }

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

    fun onAction(action: EmployeeSate.Action) {
        when (action) {
            is EmployeeSate.Action.OnDeleteStaff -> {
                deleteStaff()
            }

            is EmployeeSate.Action.OnRefresh -> {
                staffsTabManager.refreshAllTabs()
                getStaffsFlow(_uiState.value.tabIndex)
            }

            is EmployeeSate.Action.OnStaffSelected -> {
                _uiState.update { it.copy(staffSelected = action.staff) }
            }

            is EmployeeSate.Action.OnTabSelected -> {
                _uiState.update { it.copy(tabIndex = action.index) }
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
        }
    }
}

object EmployeeSate {
    data class UiState(
        val staffFilter: StaffFilter = StaffFilter(),
        val tabIndex: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val staffSelected: Staff? = null,
    )

    sealed interface Event {
        data object ShowError : Event
        data class GoToDetail(val staff: Staff) : Event
        data object GoToAddStaff : Event
        data class ShowSuccessToast(val message: String) : Event

    }

    sealed interface Action {
        data class OnStaffClicked(val staff: Staff) : Action
        data class OnTabSelected(val index: Int) : Action
        data class OnStaffSelected(val staff: Staff) : Action
        data object OnDeleteStaff : Action
        data object OnRefresh : Action
        data object OnAddStaff : Action
    }
}