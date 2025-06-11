package com.example.foodapp.domain.use_case.food_table

import com.example.foodapp.data.dto.filter.FoodTableFilter
import com.example.foodapp.domain.repository.FoodTableRepository
import javax.inject.Inject

class GetFoodTablesUseCase @Inject constructor(
    private val foodTableRepository: FoodTableRepository
) {
    operator fun invoke(filter: FoodTableFilter) = foodTableRepository.getFoodTables(filter)
}