package com.example.foodapp.domain.use_case.staff

import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.domain.repository.StaffRepository
import javax.inject.Inject

class GetStaffUseCase @Inject constructor(
    private val staffRepository: StaffRepository
) {
    operator fun invoke(filter: StaffFilter) = staffRepository.getStaffs(filter)
}