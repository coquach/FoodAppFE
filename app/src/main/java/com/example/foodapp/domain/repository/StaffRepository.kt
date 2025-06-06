package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.data.model.Staff
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StaffRepository {
    fun getStaffs(filter: StaffFilter): Flow<PagingData<Staff>>
    fun createStaff(request: Map<String, @JvmSuppressWildcards RequestBody>, image: MultipartBody.Part? = null): Flow<ApiResponse<Staff>>
    fun updateStaff(id: Long, request: Map<String, @JvmSuppressWildcards RequestBody>, image: MultipartBody.Part? = null): Flow<ApiResponse<Staff>>
    fun deleteStaff(id: Long): Flow<ApiResponse<Unit>>
}