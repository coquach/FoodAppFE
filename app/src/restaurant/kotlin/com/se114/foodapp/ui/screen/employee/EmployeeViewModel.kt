package com.se114.foodapp.ui.screen.employee

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.se114.foodapp.data.model.Staff
import com.example.foodapp.data.remote.FoodApi
import com.se114.foodapp.data.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class EmployeeViewModel
@Inject constructor(
   private val repository: StaffRepository
) : ViewModel() {

    val getAllStaffs = repository.getAllStaffs()

    private val _event = MutableSharedFlow<EmployeeEvents>()
    val event = _event.asSharedFlow()





    private val _selectedItems = mutableStateListOf<Staff>()
    val selectedItems: List<Staff> get() = _selectedItems

    fun toggleSelection(staff: Staff) {
        if (_selectedItems.contains(staff)) {
            _selectedItems.remove(staff)
        } else {
            _selectedItems.add(staff)
        }
    }

    fun selectAllItems(staffList: List<Staff>, isSelectAll: Boolean) {
        _selectedItems.clear()
        if (isSelectAll) _selectedItems.addAll(staffList)
    }

    fun removeItem() {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onRemoveClicked() {
        viewModelScope.launch {
            _event.emit(EmployeeEvents.ShowDeleteDialog)
        }
    }




    sealed class EmployeeEvents {
        data object NavigateToDetail : EmployeeEvents()
        data object ShowDeleteDialog : EmployeeEvents()
    }
}