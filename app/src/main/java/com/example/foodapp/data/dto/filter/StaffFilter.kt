package com.example.foodapp.data.dto.filter

data class StaffFilter(
    val sortBy: String = "id",
    val order: String = "desc",
    val fullName: String?= null,
    val gender: String?= null,
    val status: Boolean= true,
    val forceRefresh: String?= null,
)