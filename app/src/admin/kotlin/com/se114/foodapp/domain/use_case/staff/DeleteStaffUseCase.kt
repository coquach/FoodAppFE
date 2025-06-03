package com.se114.foodapp.domain.use_case.staff

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.domain.repository.StaffRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteStaffUseCase @Inject constructor(
    private val staffRepository: StaffRepository,
) {
    operator fun invoke(staffId: Long) = flow<ApiResponse<Unit>> {
        try {
            staffRepository.deleteStaff(staffId).collect { emit(it) }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))

        }
    }.flowOn(Dispatchers.IO)
}