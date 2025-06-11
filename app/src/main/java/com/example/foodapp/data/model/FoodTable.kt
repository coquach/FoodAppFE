package com.example.foodapp.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FoodTable(
    val id: Int? = null,
    val tableNumber: Int = 0,
    val seatCapacity: Int = 1,
    val active: Boolean= true
)