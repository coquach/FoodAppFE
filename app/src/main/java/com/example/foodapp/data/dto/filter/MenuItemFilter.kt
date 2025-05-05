package com.example.foodapp.data.dto.filter

data class MenuItemFilter (
    val id: Long?= null,
    val isAvailable: Boolean?=null,
    val reloadTrigger: Long = System.currentTimeMillis()
)