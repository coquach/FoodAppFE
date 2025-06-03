package com.se114.foodapp.domain.use_case.staff

import com.se114.foodapp.data.dto.filter.StaffFilter
import com.se114.foodapp.domain.repository.StaffRepository
import javax.inject.Inject

class GetStaffUseCase @Inject constructor(
    private val staffRepository: StaffRepository
) {
    operator fun invoke(filter: StaffFilter) = staffRepository.getStaffs(filter)
}