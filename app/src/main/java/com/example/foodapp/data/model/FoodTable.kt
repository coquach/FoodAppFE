package com.example.foodapp.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FoodTable(
    val id: Long? = null,
    val tableNumber: Int = 0,
    val seatCapacity: Int = 0,

    @SerializedName("isDeleted")
    val isDeleted: Boolean = false
)