package com.example.foodapp.data.dto.request

data class IngredientRequest (
    val name: String,
    val unitId: Long?=null
)