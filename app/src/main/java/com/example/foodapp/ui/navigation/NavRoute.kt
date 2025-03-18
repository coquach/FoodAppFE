package com.example.foodapp.ui.navigation

import com.example.foodapp.data.models.response.FoodItem
import kotlinx.serialization.Serializable


@Serializable
object Auth

@Serializable
object SignUp

@Serializable
object Login

@Serializable
object Home

@Serializable
data class FoodDetails(val foodItem: FoodItem)