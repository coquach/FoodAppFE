package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordArgs(
    val oobCode : String,
    val method: String
)
