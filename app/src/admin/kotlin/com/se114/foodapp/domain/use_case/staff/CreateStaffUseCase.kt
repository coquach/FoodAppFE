package com.se114.foodapp.domain.use_case.staff

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Staff
import com.example.foodapp.utils.ImageUtils
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.data.dto.request.StaffMultipartRequest
import com.example.foodapp.domain.repository.StaffRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateStaffUseCase @Inject constructor(
    private val staffRepository: StaffRepository,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(staff: Staff) = flow<ApiResponse<Staff>> {
        emit(ApiResponse.Loading)
        try {
            val request = StaffMultipartRequest(
                fullName = staff.fullName,
                position = staff.position!!,
                phone = staff.phone,
                gender = staff.gender,
                address = staff.address,
                birthDate = StringUtils.formatLocalDate(staff.birthDate)!!,
                startDate = StringUtils.formatLocalDate(staff.startDate)!!,
                endDate = StringUtils.formatLocalDate(staff.endDate)!!,
                basicSalary = staff.basicSalary
            )
            val partMap = request.toPartMap()
            val avatar = ImageUtils.getImagePart(context, staff.avatar?.url?.toUri() )
            staffRepository.createStaff(partMap, avatar).collect { emit(it) }
        }catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))
        }
    }.flowOn(Dispatchers.IO)
}