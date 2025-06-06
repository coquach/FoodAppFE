package com.se114.foodapp.domain.use_case.supplier

import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.se114.foodapp.domain.repository.SupplierRepository
import javax.inject.Inject

class GetSupplierUseCase @Inject constructor(
    private val supplierRepository: SupplierRepository
) {
    operator fun invoke(filter: SupplierFilter) = supplierRepository.getSuppliers(filter)
}