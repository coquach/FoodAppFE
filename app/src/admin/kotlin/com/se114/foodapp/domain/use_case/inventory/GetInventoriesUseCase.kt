package com.se114.foodapp.domain.use_case.inventory

import com.se114.foodapp.data.dto.filter.InventoryFilter
import com.se114.foodapp.domain.repository.ImportRepository
import javax.inject.Inject

class GetInventoriesUseCase @Inject constructor(
    private val importRepository: ImportRepository
) {
    operator fun invoke(filter: InventoryFilter) = importRepository.getInventories(filter)
}