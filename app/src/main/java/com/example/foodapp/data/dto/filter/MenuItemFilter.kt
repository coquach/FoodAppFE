package com.example.foodapp.data.dto.filter

data class FoodFilter (
    val id: Long?= null,
    val isAvailable: Boolean?=null,
    val reloadTrigger: Long = System.currentTimeMillis()
)