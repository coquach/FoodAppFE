package com.example.foodapp.data.dto.request

data class SignUpRequest(
    val fullName: String,
    val username: String,
    val email: String,
    val password: String,
    val phoneNumber: String
)
