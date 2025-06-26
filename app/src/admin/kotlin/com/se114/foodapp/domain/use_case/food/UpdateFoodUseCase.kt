package com.se114.foodapp.domain.use_case.food

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Food
import com.example.foodapp.domain.repository.FoodRepository
import com.example.foodapp.utils.ImageUtils
import com.se114.foodapp.data.dto.request.FoodMultipartRequest
import com.se114.foodapp.ui.screen.menu.food_details.FoodAddUi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateFoodUseCase @Inject constructor(
    private val foodRepository: FoodRepository,
    @ApplicationContext val context: android.content.Context,
) {
    operator fun invoke(food: FoodAddUi) = flow<ApiResponse<Food>> {

        try {
            emit(ApiResponse.Loading)
            val request = FoodMultipartRequest(
                name = food.name,
                description = food.description,
                price = food.price,
                menuId = food.menuId,
            )
            val foodId = food.id!!
            val imageParts = food.images?.map { ImageUtils.getImagePart(context, it) }
            val partMap = request.toPartMap()
            foodRepository.updateFood(foodId, partMap, imageParts).collect {
                emit(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Unknown error", 401))

        }

    }.flowOn(Dispatchers.IO)
}