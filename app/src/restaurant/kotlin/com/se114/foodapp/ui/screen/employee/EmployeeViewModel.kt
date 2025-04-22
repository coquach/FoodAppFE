package com.se114.foodapp.ui.screen.employee

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.Staff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<EmployeeState>(EmployeeState.Nothing)
    val uiState = _uiState.asStateFlow()

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
    sealed class EmployeeState {
        data object Nothing : EmployeeState()
        data object Loading : EmployeeState()
        data class Success(val staff: List<Staff>) :
            EmployeeState()

        data class Error(val message: String) : EmployeeState()
    }

    sealed class EmployeeEvents {
        data object NavigateToDetail : EmployeeEvents()
        data object ShowDeleteDialog : EmployeeEvents()
    }
}