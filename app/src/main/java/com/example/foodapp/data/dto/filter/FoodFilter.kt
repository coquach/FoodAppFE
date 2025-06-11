package com.example.foodapp.data.dto.filter

data class FoodFilter(
    val sortBy: String = "id",
    val order: String = "asc",
    val name: String? = null,
    val status: Boolean? = null,
    val menuId: Int ?= null,
)