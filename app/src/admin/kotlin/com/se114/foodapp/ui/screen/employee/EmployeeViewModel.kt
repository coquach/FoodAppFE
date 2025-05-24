package com.se114.foodapp.ui.screen.employee

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.repository.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPagingApi::class)
class EmployeeViewModel
@Inject constructor(
    private val foodApi: FoodApi,
    private val staffRepository: StaffRepository
) : ViewModel() {

    private val _staffList = MutableStateFlow<PagingData<Staff>>(PagingData.empty())
    val staffList = _staffList

    init {
        refreshStaff()
    }

    fun refreshStaff() {
        viewModelScope.launch {
            staffRepository.getAllStaffs().cachedIn(viewModelScope).collect{
                _staffList.value = it
            }
        }
    }


    private val _event = MutableSharedFlow<EmployeeEvents>()
    val event = _event.asSharedFlow()


    fun removeItem(staffId: Long) {
        viewModelScope.launch {
            try {

                val response = safeApiCall { foodApi.deleteStaff(staffId) }
                when (response) {
                    is ApiResponse.Success -> {
                        _event.emit(EmployeeEvents.ShowSuccessToast("Xóa nhân viên thành công"))
                    }
                    is ApiResponse.Error -> {
                        _event.emit(EmployeeEvents.ShowErrorMessage("Xóa nhân viên thất bại: ${response.message}"))
                    }
                    else -> {
                        _event.emit(EmployeeEvents.ShowErrorMessage("Có lỗi xảy ra"))
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Api error: ", "${e.message}")
                _event.emit(EmployeeEvents.ShowErrorMessage("Có lỗi xảy ra: ${e.message}"))
            }
        }
    }

    fun onRemoveClicked() {
        viewModelScope.launch {
            _event.emit(EmployeeEvents.ShowDeleteDialog)
        }
    }



    sealed class EmployeeEvents {
        data class ShowSuccessToast(val message: String): EmployeeEvents()
        data object ShowDeleteDialog : EmployeeEvents()
        data class ShowErrorMessage(val message: String) : EmployeeEvents()
    }
}