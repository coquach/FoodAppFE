package com.se114.foodapp.domain.use_case.staff

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Staff
import com.example.foodapp.domain.repository.StaffRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TerminateStaffUseCase @Inject constructor(
    private val staffRepository: StaffRepository,
) {
    operator fun invoke(staffId: Long) = flow<ApiResponse<Staff>> {
        try {
            staffRepository.terminateStaff(staffId).collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(ApiResponse.Failure("Đã xảy ra lỗi khi ngưng việc nhân viên", 999))

        }
    }.flowOn(Dispatchers.IO)
}