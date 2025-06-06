package com.example.foodapp.domain.use_case.food

import androidx.paging.PagingData
import com.example.foodapp.data.model.Food
import com.example.foodapp.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFoodsByMenuIdUseCase @Inject constructor(
    private val foodRepository: FoodRepository

) {
    operator fun invoke(menuId: Long) : Flow<PagingData<Food>>{
        return foodRepository.getFoodsByMenuId(menuId)
    }
}