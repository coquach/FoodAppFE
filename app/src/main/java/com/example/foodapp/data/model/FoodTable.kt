package com.example.foodapp.data.model

import com.example.foodapp.data.model.enums.FoodTableStatus
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FoodTable(
    val id: Int? = null,
    val tableNumber: Int = 0,
    val seatCapacity: Int = 1,
    val status: String = FoodTableStatus.EMPTY.name,
    val active: Boolean= true
)