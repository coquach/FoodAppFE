package com.se114.foodapp.domain.use_case.export

import com.se114.foodapp.data.dto.filter.ExportFilter
import com.se114.foodapp.domain.repository.ExportRepository
import javax.inject.Inject

class GetExportsUseCase @Inject constructor(
    private val exportRepository: ExportRepository
) {
    operator fun invoke(filter: ExportFilter) = exportRepository.getExports(filter)
}