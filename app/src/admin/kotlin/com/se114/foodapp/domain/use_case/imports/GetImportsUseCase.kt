package com.se114.foodapp.domain.use_case.imports

import com.se114.foodapp.data.dto.filter.ImportFilter
import com.se114.foodapp.domain.repository.ImportRepository
import javax.inject.Inject

class GetImportsUseCase @Inject constructor(
    private val importRepository: ImportRepository
)  {
    operator fun invoke(filter: ImportFilter) = importRepository.getImports(filter)
}