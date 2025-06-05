package com.example.foodapp.domain.use_case.inventory

import com.example.foodapp.data.dto.filter.InventoryFilter
import com.example.foodapp.domain.repository.InventoryRepository
import javax.inject.Inject

class GetInventoriesUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    operator fun invoke(filter: InventoryFilter) = inventoryRepository.getInventoriesByFilter(filter)
}