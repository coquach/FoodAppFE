package com.example.foodapp.data.dto.request

data class FcmTokenRequest(
    val userId: String,
    val userType: String,
    val token: String
)
