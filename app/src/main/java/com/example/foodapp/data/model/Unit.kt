package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Unit(
    val id: Long,
    val name: String
)