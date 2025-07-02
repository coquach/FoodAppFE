package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IntentType(
    val id: Long?= null,
    val name: String= ""
)
