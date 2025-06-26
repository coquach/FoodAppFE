package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val id: Int?=null,
    val name: String="",
    val active: Boolean= true
)