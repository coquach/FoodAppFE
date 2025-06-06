package com.se114.foodapp.data.mapper

import androidx.core.net.toUri
import com.example.foodapp.data.model.Food
import com.se114.foodapp.ui.screen.menu.food_details.FoodAddUi

fun Food.toFoodAddUi() = FoodAddUi (
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        images = this.images?.map { it.url.toUri() },
        defaultQuantity = defaultQuantity
    )
