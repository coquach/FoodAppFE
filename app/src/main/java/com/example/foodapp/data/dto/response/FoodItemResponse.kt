package com.example.foodapp.data.dto.response

import com.example.foodapp.data.model.FoodItem
import kotlinx.serialization.Serializable


data class FoodItemResponse(
    val foodItems: List<FoodItem>
)