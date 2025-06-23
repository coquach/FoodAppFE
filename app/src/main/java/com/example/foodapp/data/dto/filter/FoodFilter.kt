package com.example.foodapp.data.dto.filter

data class FoodFilter(
    val sortBy: String = "id",
    val order: String = "desc",
    val name: String? = null,
    val status: Boolean=  true,
    val menuId: Int ?= null,
)