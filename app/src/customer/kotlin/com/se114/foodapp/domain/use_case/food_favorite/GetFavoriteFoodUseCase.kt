package com.se114.foodapp.domain.use_case.food_favorite

import com.example.foodapp.data.dto.filter.FoodFilter
import com.example.foodapp.domain.repository.FoodRepository
import javax.inject.Inject

class GetFavoriteFoodUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    operator fun invoke(foodFilter: FoodFilter) = foodRepository.getFavoriteFoods(foodFilter)

}