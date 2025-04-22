package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val id: Long? = null,
    val name: String,
    val unit: Unit,
    val importDetails: List<ImportDetail> = mutableListOf(),
    val inventories: List<Inventory> = mutableListOf(),
    val isDeleted: Boolean = false
)
