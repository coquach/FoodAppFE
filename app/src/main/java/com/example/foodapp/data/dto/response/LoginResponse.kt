package com.example.foodapp.data.dto.response

data class AuthResponse(
    val data: TokenData
) : BaseResponse(200, "Success")
data class TokenData(
    val accessToken: String,
    val refreshToken: String
)